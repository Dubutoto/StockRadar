import schedule
import time
import requests
import mysql.connector
import mysql.connector.pooling  # 커넥션 풀 사용

from url_config import URL_TASKS
from crawler import get_amazon_data, get_ssg_data, get_11st_data

# === 커넥션 풀 생성 ===
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
    커넥션 풀에서 conn을 하나 가져와서 DB 작업 후 반환
    """
    conn = pool.get_connection()
    cursor = conn.cursor()

    # 1) category
    select_cat_sql = "SELECT category_id FROM category WHERE category_name = %s"
    cursor.execute(select_cat_sql, (category_name,))
    cat_row = cursor.fetchone()

    if cat_row:
        category_id = cat_row[0]
    else:
        insert_cat_sql = "INSERT INTO category (category_name) VALUES (%s)"
        cursor.execute(insert_cat_sql, (category_name,))
        category_id = cursor.lastrowid

    # 2) store
    select_store_sql = "SELECT store_id FROM store WHERE store_name = %s"
    cursor.execute(select_store_sql, (store_name,))
    store_row = cursor.fetchone()

    if store_row:
        store_id = store_row[0]
    else:
        insert_store_sql = "INSERT INTO store (store_name) VALUES (%s)"
        cursor.execute(insert_store_sql, (store_name,))
        store_id = cursor.lastrowid

    # 3) product
    select_product_sql = "SELECT product_id FROM product WHERE product_url = %s"
    cursor.execute(select_product_sql, (product_url,))
    prod_row = cursor.fetchone()

    if prod_row:
        product_id = prod_row[0]
        update_product_sql = """
            UPDATE product
            SET product_name = %s, category_id = %s, store_id = %s
            WHERE product_id = %s
        """
        cursor.execute(update_product_sql, (product_name, category_id, store_id, product_id))
    else:
        insert_product_sql = """
            INSERT INTO product (product_name, product_url, category_id, store_id)
            VALUES (%s, %s, %s, %s)
        """
        cursor.execute(insert_product_sql, (product_name, product_url, category_id, store_id))
        product_id = cursor.lastrowid

    # 4) stock_status
    availability_value = 1 if in_stock else 0

    select_stock_sql = "SELECT stock_id FROM stock_status WHERE product_id = %s"
    cursor.execute(select_stock_sql, (product_id,))
    stock_row = cursor.fetchone()

    if stock_row:
        stock_id = stock_row[0]
        update_stock_sql = """
            UPDATE stock_status
            SET availability = %s, last_updated = NOW()
            WHERE stock_id = %s
        """
        cursor.execute(update_stock_sql, (availability_value, stock_id))
    else:
        insert_stock_sql = """
            INSERT INTO stock_status (availability, last_updated, product_id)
            VALUES (%s, NOW(), %s)
        """
        cursor.execute(insert_stock_sql, (availability_value, product_id))
        stock_id = cursor.lastrowid

    # 5) price
    price_int = int(price)

    select_price_sql = "SELECT price_id FROM price WHERE stock_id = %s"
    cursor.execute(select_price_sql, (stock_id,))
    price_row = cursor.fetchone()

    if price_row:
        price_id = price_row[0]
        update_price_sql = """
            UPDATE price
            SET price = %s, last_update = NOW()
            WHERE price_id = %s
        """
        cursor.execute(update_price_sql, (price_int, price_id))
    else:
        insert_price_sql = """
            INSERT INTO price (price, last_update, stock_id)
            VALUES (%s, NOW(), %s)
        """
        cursor.execute(insert_price_sql, (price_int, stock_id))

    conn.commit()
    cursor.close()
    conn.close()

def crawl_one(task):
    """
    단일 URL 크롤링 + DB 저장 (단일 스레드)
    """
    category_name = task["categoryName"]
    store_name = task["storeName"]
    product_url = task["url"]

    with requests.Session() as session:
        if store_name == "Amazon":
            product_name, in_stock, price = get_amazon_data(session, product_url)
        elif store_name == "SSG":
            product_name, in_stock, price = get_ssg_data(session, product_url)
        elif store_name == "11ST":
            product_name, in_stock, price = get_11st_data(session, product_url)
        else:
            product_name, in_stock, price = ("Unknown Product", False, 0)

    print(f"[{store_name}] 크롤링 결과 -> {product_name}, 재고: {in_stock}, 가격: {price}")
    save_to_db(category_name, store_name, product_name, product_url, in_stock, price)

    return (product_url, product_name, in_stock, price)

def job():
    print("\n=== job() 함수 실행 (단일 스레드) ===")
    start_time = time.perf_counter()

    # 단순 순차 실행
    for task in URL_TASKS:
        crawl_one(task)

    end_time = time.perf_counter()
    elapsed = end_time - start_time
    print(f"=== 모든 URL 크롤링 및 DB 저장 완료! 총 {len(URL_TASKS)}건, 소요 시간: {elapsed:.2f}초 ===")

if __name__ == "__main__":
    print("Requests + BeautifulSoup + New Entity 크롤링 서버 (단일 스레드) 시작!")
    job()
    # 스케줄링 예시
    schedule.every(10).minutes.do(job)

    while True:
        schedule.run_pending()
        time.sleep(1)
