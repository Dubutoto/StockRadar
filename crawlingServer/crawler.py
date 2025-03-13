import requests
import random
import time
import re
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# 무작위 User-Agent 목록
USER_AGENTS = [
    # Windows 기반
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:110.0) Gecko/20100101 Firefox/110.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.0.0",
    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
    "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko",

    # macOS 기반
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.1 Safari/605.1.15",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:95.0) Gecko/20100101 Firefox/95.0",
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
    Amazon 상품 정보 크롤링 (requests + BeautifulSoup 사용)
    - 상품명: <span id="productTitle">
    - 재고 여부: "in stock" 또는 "only" 포함 여부
    - 가격: <span class="a-price-whole">에서 점(.) 앞의 숫자만 추출
    """
    print("[DEBUG] Amazon 크롤링(requests+BeautifulSoup) 시작:", url)
    product_name = "Unknown Product"
    in_stock = False
    price_value = 0.0

    try:
        headers = get_random_headers()  # 기존의 무작위 헤더 함수 사용
        response = requests.get(url, headers=headers, timeout=15)
        if response.status_code != 200:
            print(f"[ERROR] Amazon 페이지 요청 실패, 상태 코드: {response.status_code}")
            return product_name, in_stock, price_value

        soup = BeautifulSoup(response.text, "html.parser")

        # 1) 상품명 추출
        product_name_elem = soup.find("span", id="productTitle")
        if product_name_elem:
            product_name = product_name_elem.get_text(strip=True)
        else:
            print("[WARNING] Amazon 상품명 추출 실패")

        # 2) 재고 여부 확인
        availability_elem = soup.find("div", id="availability")
        if availability_elem:
            availability_text = availability_elem.get_text(strip=True).lower()
            if "in stock" in availability_text or "only" in availability_text:
                in_stock = True
        else:
            print("[WARNING] Amazon 재고 정보 추출 실패")

        # 3) 가격 정보 추출 (정수 부분만 사용)
        price_whole_elem = soup.find("span", class_="a-price-whole")
        if price_whole_elem:
            price_whole_text = price_whole_elem.get_text(strip=True)
            # 점(.) 앞의 부분만 추출
            price_before_dot = price_whole_text.split('.')[0]
            # 숫자 이외의 문자는 제거
            price_digits = re.sub(r'\D', '', price_before_dot)
            try:
                if price_digits:
                    price_value = float(price_digits)
                else:
                    price_value = 0.0
            except Exception as e:
                print("[WARNING] 가격 파싱 실패:", e)
                price_value = 0.0
        else:
            print("[WARNING] Amazon 가격 정보 추출 실패")
            price_value = 0.0

        # 가격이 0이면 재고 없음으로 처리
        if price_value == 0.0:
            in_stock = False

        print(f"[INFO] Amazon 크롤링 결과 -> 상품명: {product_name}, 재고: {in_stock}, 가격: {price_value}")
    except Exception as e:
        print(f"[ERROR] Amazon 크롤링 실패: {e}")

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
            
    if price_value ==0:
        in_stock = False     

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

    if price_value ==0:
        in_stock = False   
        
    print(f"[DEBUG] 11ST -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
    return product_name, in_stock, price_value