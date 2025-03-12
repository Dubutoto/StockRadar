import requests
import random
import time
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# 무작위 User-Agent 목록
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:110.0) Gecko/20100101 Firefox/110.0",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_0) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
]

def get_random_headers():
    ua = random.choice(USER_AGENTS)
    return {
        "User-Agent": ua,
        "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        "Accept-Encoding": "gzip, deflate, br"
    }

def fetch_with_retries(session, url, max_retries=3, timeout=15):
    """
    무작위 지연 + 재시도
    """
    for attempt in range(max_retries):
        try:
            time.sleep(random.randint(1, 3))
            headers = get_random_headers()
            response = session.get(url, headers=headers, timeout=timeout)
            return response
        except requests.exceptions.RequestException as e:
            print(f"[fetch_with_retries] {url} 실패 ({attempt+1}/{max_retries}): {e}")
            if attempt < max_retries - 1:
                time.sleep(random.randint(2, 5))
            else:
                return None
    return None

def get_amazon_data(url):
    """
    Amazon 상품 정보 크롤링 (Selenium 사용)
    - 상품명: <span id="productTitle">
    - 재고 여부: "in stock", "In Stock", "only" 포함 여부 (가격 없으면 재고 없음)
    - 가격: <span class="a-price-whole"> + <span class="a-price-decimal">
    """
    print("[DEBUG] Amazon 크롤링(Selenium) 시작:", url)
    product_name = "Unknown Product"
    in_stock = False
    price_value = 0.0

    # Selenium 옵션 설정 (헤드리스 모드)
    chrome_options = Options()
    chrome_options.add_argument("--headless")  # 브라우저 UI 없이 실행
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("window-size=1920x1080")

    # 웹드라이버 실행
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=chrome_options)

    try:
        driver.get(url)
        time.sleep(3)  # 페이지 로딩 대기

        # 1) 상품명 가져오기
        try:
            product_name_elem = driver.find_element(By.ID, "productTitle")
            product_name = product_name_elem.text.strip()
        except Exception:
            print("[WARNING] Amazon 상품명 추출 실패")

        # 2) 재고 여부 확인
        try:
            availability_elem = driver.find_element(By.ID, "availability")
            availability_text = availability_elem.text.strip().lower()
            if "in stock" in availability_text or "only" in availability_text:
                in_stock = True
        except Exception:
            print("[WARNING] Amazon 재고 정보 추출 실패")

        # 3) 가격 가져오기 (할인율 제외)
        try:
            price_whole = driver.find_element(By.CLASS_NAME, "a-price-whole").text.strip()
            price_decimal = driver.find_element(By.CLASS_NAME, "a-price-decimal").text.strip()
            price_value = float(f"{price_whole}{price_decimal}")
        except Exception:
            print("[WARNING] Amazon 가격 정보 추출 실패")
            price_value = 0.0  # 가격 정보 없으면 0으로 설정

        # 💡 가격이 없으면 재고 없음으로 설정
        if price_value == 0.0:
            in_stock = False

        print(f"[INFO] Amazon 크롤링 결과 -> 상품명: {product_name}, 재고: {in_stock}, 가격: {price_value}")

    except Exception as e:
        print(f"[ERROR] Amazon 크롤링 실패: {e}")

    finally:
        driver.quit()  # 드라이버 종료

    return product_name, in_stock, price_value

def get_ssg_data(session, url):
    """
    SSG 크롤링
    - 상품명: <span.cdtl_info_tit_txt> or <h2.cdtl_info_tit>
    - 재고: "일시품절" 단어가 있으면 False
    - 가격: <em.ssg_price> or <span.ssg_price>
    """
    print("[DEBUG] SSG 크롤링(BeautifulSoup) 시작:", url)
    product_name = "Unknown Product"
    in_stock = True
    price_value = 0

    response = fetch_with_retries(session, url)
    if not response or response.status_code != 200:
        print(f"[SSG] 요청 실패 (status_code={response.status_code if response else 'None'})")
        return product_name, in_stock, price_value

    soup = BeautifulSoup(response.text, "html.parser")

    # 1) 상품명
    title_elem = soup.select_one("span.cdtl_info_tit_txt") or soup.select_one("h2.cdtl_info_tit")
    if title_elem:
        product_name = title_elem.get_text(strip=True)

    # 2) "일시품절" 체크
    page_text = soup.get_text(separator=" ", strip=True)
    if "일시품절" in page_text:
        in_stock = False

    # 3) 가격
    price_elem = soup.select_one("em.ssg_price") or soup.select_one("span.ssg_price")
    if price_elem:
        price_text = price_elem.get_text(strip=True).replace(",", "")
        digits = "".join([c for c in price_text if c.isdigit()])
        if digits.isdigit():
            price_value = int(digits)

    print(f"[DEBUG] SSG -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
    return product_name, in_stock, price_value

def get_11st_data(session, url):
    """
    11번가 크롤링
    - 상품명: <h1 class="title">...</h1>
    - 재고: <span class="text_em_sm">품절</span> 있으면 False
    - 가격: <span class="value">611,000</span>
    """
    print("[DEBUG] 11번가 크롤링(BeautifulSoup) 시작:", url)
    product_name = "Unknown Product"
    in_stock = True
    price_value = 0

    response = fetch_with_retries(session, url)
    if not response or response.status_code != 200:
        print(f"[11ST] 요청 실패 (status_code={response.status_code if response else 'None'})")
        return product_name, in_stock, price_value

    soup = BeautifulSoup(response.text, "html.parser")

    # 1) 상품명
    title_elem = soup.select_one("h1.title")
    if title_elem:
        product_name = title_elem.get_text(strip=True)

    # 2) 재고 여부
    soldout_elem = soup.select_one("span.text_em_sm")
    if soldout_elem:
        soldout_text = soldout_elem.get_text(strip=True)
        if "품절" in soldout_text:
            in_stock = False

    # 3) 가격
    price_elem = soup.select_one("dd.price span.value")
    if price_elem:
        price_text = price_elem.get_text(strip=True).replace(",", "")
        digits = "".join([c for c in price_text if c.isdigit()])
        if digits.isdigit():
            price_value = int(digits)

    print(f"[DEBUG] 11ST -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
    return product_name, in_stock, price_value