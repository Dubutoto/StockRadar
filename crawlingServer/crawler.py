import requests
import random
import time
from bs4 import BeautifulSoup

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

def get_amazon_data(session, url):
    """
    Amazon 크롤링
    - 상품명: #productTitle
    - 재고: #availability 안의 텍스트에 "in stock", "only", "order soon"
    - 가격: <span class="a-offscreen">$526.89</span>
    """
    print("[DEBUG] Amazon 크롤링(BeautifulSoup) 시작:", url)
    product_name = "Unknown Product"
    in_stock = False
    price_value = 0.0

    response = fetch_with_retries(session, url)
    if not response or response.status_code != 200:
        print(f"[Amazon] 요청 실패 (status_code={response.status_code if response else 'None'})")
        return product_name, in_stock, price_value

    soup = BeautifulSoup(response.text, "html.parser")

    # 1) 상품명
    title_elem = soup.select_one("#productTitle")
    if title_elem:
        product_name = title_elem.get_text(strip=True)

    # 2) 재고 여부
    availability_elem = soup.select_one("#availability")
    if availability_elem:
        availability_text = availability_elem.get_text(strip=True).lower()
        if ("in stock" in availability_text
            or "only" in availability_text
            or "order soon" in availability_text):
            in_stock = True

    # 3) 가격
    price_elem = soup.select_one("span.a-offscreen")
    if price_elem:
        price_text = price_elem.get_text(strip=True).replace("$", "").replace(",", "")
        try:
            price_value = float(price_text)
        except ValueError:
            price_value = 0.0

    print(f"[DEBUG] Amazon -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
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
