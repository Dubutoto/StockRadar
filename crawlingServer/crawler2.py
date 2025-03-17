# crawler2.py
import asyncio
import aiohttp
import random
import re
from bs4 import BeautifulSoup

# Windows와 macOS 기반의 User-Agent 10개 목록
USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:110.0) Gecko/20100101 Firefox/110.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.0.0",
    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
    "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.1 Safari/605.1.15",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:95.0) Gecko/20100101 Firefox/95.0",
]

def get_random_headers():
    return {
        "User-Agent": random.choice(USER_AGENTS),
        "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        "Accept-Encoding": "gzip, deflate, br"
    }

async def fetch(session, url):
    # 요청 전에 랜덤 딜레이를 추가하여 서버 부하 완화
    await asyncio.sleep(random.uniform(0.5, 2.0))
    headers = get_random_headers()
    try:
        async with session.get(url, headers=headers, timeout=15) as response:
            if response.status != 200:
                print(f"[ERROR] 요청 실패, 상태 코드: {response.status} for {url}")
                return None
            return await response.text()
    except Exception as e:
        print(f"[ERROR] 요청 예외: {e} for {url}")
        return None

async def parse_ssg(session, task):
    url = task["url"]
    print("[DEBUG] SSG 크롤링(비동기) 시작:", url)
    html = await fetch(session, url)
    if html is None:
        print(f"[SSG] 요청 실패 (html is None) for {url}")
        return (url, "Unknown Product", True, 0)
    soup = BeautifulSoup(html, "html.parser")
    
    product_name = "Unknown Product"
    in_stock = True
    price_value = 0
    
    # 상품명 추출
    title_elem = soup.select_one("span.cdtl_info_tit_txt") or soup.select_one("h2.cdtl_info_tit")
    if title_elem:
        product_name = title_elem.get_text(strip=True)
    
    # 재고 여부: '일시품절' 문구가 있으면 품절 처리
    page_text = soup.get_text(separator=" ", strip=True)
    if "일시품절" in page_text:
        in_stock = False
    
    # 가격 정보 추출
    price_elem = soup.select_one("em.ssg_price") or soup.select_one("span.ssg_price")
    if price_elem:
        price_text = price_elem.get_text(strip=True).replace(",", "")
        digits = "".join(c for c in price_text if c.isdigit())
        if digits.isdigit():
            price_value = int(digits)
    if price_value == 0:
        in_stock = False

    print(f"[DEBUG] SSG -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
    return (url, product_name, in_stock, price_value)

async def parse_11st(session, task):
    url = task["url"]
    print("[DEBUG] 11번가 크롤링(비동기) 시작:", url)
    html = await fetch(session, url)
    if html is None:
        print(f"[11ST] 요청 실패 (html is None) for {url}")
        return (url, "Unknown Product", True, 0)
    soup = BeautifulSoup(html, "html.parser")
    
    product_name = "Unknown Product"
    in_stock = True
    price_value = 0
    
    title_elem = soup.select_one("h1.title")
    if title_elem:
        product_name = title_elem.get_text(strip=True)
    
    soldout_elem = soup.select_one("span.text_em_sm")
    if soldout_elem:
        soldout_text = soldout_elem.get_text(strip=True)
        if "품절" in soldout_text:
            in_stock = False

    price_elem = soup.select_one("dd.price span.value")
    if price_elem:
        price_text = price_elem.get_text(strip=True).replace(",", "")
        digits = "".join(c for c in price_text if c.isdigit())
        if digits.isdigit():
            price_value = int(digits)
    if price_value == 0:
        in_stock = False

    print(f"[DEBUG] 11ST -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
    return (url, product_name, in_stock, price_value)

async def parse_amazon(session, task):
    url = task["url"]
    print("[DEBUG] Amazon 크롤링(비동기) 시작:", url)
    html = await fetch(session, url)
    if html is None:
        print(f"[Amazon] 요청 실패 (html is None) for {url}")
        return (url, "Unknown Product", False, 0.0)
    soup = BeautifulSoup(html, "html.parser")
    
    product_name = "Unknown Product"
    in_stock = False
    price_value = 0.0
    
    product_name_elem = soup.find("span", id="productTitle")
    if product_name_elem:
        product_name = product_name_elem.get_text(strip=True)
    else:
        print("[WARNING] Amazon 상품명 추출 실패 for", url)
    
    availability_elem = soup.find("div", id="availability")
    if availability_elem:
        availability_text = availability_elem.get_text(strip=True).lower()
        if "in stock" in availability_text or "only" in availability_text:
            in_stock = True
    else:
        print("[WARNING] Amazon 재고 정보 추출 실패 for", url)
    
    price_whole_elem = soup.find("span", class_="a-price-whole")
    if price_whole_elem:
        price_whole_text = price_whole_elem.get_text(strip=True)
        # '.' 앞의 정수 부분만 추출
        price_before_dot = price_whole_text.split('.')[0]
        price_digits = re.sub(r'\D', '', price_before_dot)
        try:
            if price_digits:
                price_value = float(price_digits)
            else:
                price_value = 0.0
        except Exception as e:
            print("[WARNING] 가격 파싱 실패 for", url, ":", e)
            price_value = 0.0
    else:
        print("[WARNING] Amazon 가격 정보 추출 실패 for", url)
    if price_value == 0.0:
        in_stock = False

    print(f"[INFO] Amazon 크롤링 결과 -> {product_name}, 재고: {in_stock}, 가격: {price_value}")
    return (url, product_name, in_stock, price_value)

async def process_task(session, task):
    store = task["storeName"]
    if store == "SSG":
        return await parse_ssg(session, task)
    elif store == "11ST":
        return await parse_11st(session, task)
    elif store == "Amazon":
        return await parse_amazon(session, task)
    else:
        return (task["url"], "Unknown Product", False, 0)
