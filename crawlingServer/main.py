import schedule
import time
import requests
import mysql.connector
import mysql.connector.pooling  # 커넥션 풀 사용
from concurrent.futures import ThreadPoolExecutor

from crawler import get_amazon_data, get_ssg_data, get_11st_data
from url_config import URL_TASKS

# === 1) 커넥션 풀 생성 ===
dbconfig = {
    "host": "localhost",
    "user": "root",
    "password": "Dubutoto22!",
    "database": "stockradar"
}

pool = mysql.connector.pooling.MySQLConnectionPool(
    pool_name="mypool",
    pool_size=5,
    pool_reset_session=True,
    **dbconfig
)

def save_to_db(category_name, store_name, product_name, product_url, in_stock, price):
    """
    커넥션 풀에서 conn을 하나 가져와서 DB 작업 후 반환.
    """
    conn = pool.get_connection()
    cursor = conn.cursor()

    try:
        # 1) category
        cursor.execute("SELECT category_id FROM category WHERE category_name = %s", (category_name,))
        cat_row = cursor.fetchone()

        if cat_row:
            category_id = cat_row[0]
        else:
            cursor.execute("INSERT INTO category (category_name) VALUES (%s)", (category_name,))
            category_id = cursor.lastrowid

        # 2) store
        cursor.execute("SELECT store_id FROM store WHERE store_name = %s", (store_name,))
        store_row = cursor.fetchone()

        if store_row:
            store_id = store_row[0]
        else:
            cursor.execute("INSERT INTO store (store_name) VALUES (%s)", (store_name,))
            store_id = cursor.lastrowid

        # 3) product
        cursor.execute("SELECT product_id FROM product WHERE product_url = %s", (product_url,))
        prod_row = cursor.fetchone()

        if prod_row:
            product_id = prod_row[0]
            cursor.execute("""
                UPDATE product
                SET product_name = %s, category_id = %s, store_id = %s
                WHERE product_id = %s
            """, (product_name, category_id, store_id, product_id))
        else:
            cursor.execute("""
                INSERT INTO product (product_name, product_url, category_id, store_id)
                VALUES (%s, %s, %s, %s)
            """, (product_name, product_url, category_id, store_id))
            product_id = cursor.lastrowid

        # 4) stock_status
        availability_value = 1 if in_stock else 0
        cursor.execute("SELECT stock_id FROM stock_status WHERE product_id = %s", (product_id,))
        stock_row = cursor.fetchone()

        if stock_row:
            stock_id = stock_row[0]
            cursor.execute("""
                UPDATE stock_status
                SET availability = %s, last_updated = NOW()
                WHERE stock_id = %s
            """, (availability_value, stock_id))
        else:
            cursor.execute("""
                INSERT INTO stock_status (availability, last_updated, product_id)
                VALUES (%s, NOW(), %s)
            """, (availability_value, product_id))
            stock_id = cursor.lastrowid

        # 5) price
        cursor.execute("SELECT price_id FROM price WHERE stock_id = %s", (stock_id,))
        price_row = cursor.fetchone()

        price_int = int(price)

        if price_row:
            price_id = price_row[0]
            cursor.execute("""
                UPDATE price
                SET price = %s, last_update = NOW()
                WHERE price_id = %s
            """, (price_int, price_id))
        else:
            cursor.execute("""
                INSERT INTO price (price, last_update, stock_id)
                VALUES (%s, NOW(), %s)
            """, (price_int, stock_id))

        conn.commit()

    except Exception as e:
        print(f"[ERROR] DB 저장 중 오류 발생: {e}")
        conn.rollback()

    finally:
        cursor.close()
        conn.close()

def crawl_one(task):
    """
    단일 URL 크롤링 + DB 저장 (스레드에서 병렬 실행)
    """
    category_name = task["categoryName"]
    store_name = task["storeName"]
    product_url = task["url"]

    with requests.Session() as session:
        if store_name == "Amazon":
            product_name, in_stock, price = get_amazon_data(product_url)  # Selenium 사용
        elif store_name == "SSG":
            product_name, in_stock, price = get_ssg_data(session, product_url)
        elif store_name == "11ST":
            product_name, in_stock, price = get_11st_data(session, product_url)
        else:
            product_name, in_stock, price = ("Unknown Product", False, 0)

    print(f"[{store_name}] 크롤링 -> {product_name}, 재고: {in_stock}, 가격: {price}")
    save_to_db(category_name, store_name, product_name, product_url, in_stock, price)

    return (product_url, product_name, in_stock, price)

def job():
    """
    멀티스레드 크롤링 작업 실행
    """
    print("\n=== job() 함수 실행 (멀티스레드 + 커넥션 풀) ===")
    start_time = time.perf_counter()

    max_workers = 5
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        results = list(executor.map(crawl_one, URL_TASKS))

    end_time = time.perf_counter()
    elapsed = end_time - start_time
    print(f"=== 모든 URL 크롤링 및 DB 저장 완료! 총 {len(results)}건, 소요 시간: {elapsed:.2f}초 ===")

if __name__ == "__main__":
    print("Requests + BeautifulSoup + Selenium 크롤링 서버 시작!")
    job()
    schedule.every(5).minutes.do(job)

    while True:
        schedule.run_pending()
        time.sleep(1)
