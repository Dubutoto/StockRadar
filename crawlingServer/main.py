import schedule
import time
import random
import mysql.connector
import requests

# 크롤링 함수
from crawler import get_amazon_data, get_ssg_data, get_11st_data
# URL 목록을 별도 파일로 분리
from url_config import URL_TASKS

db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="1234",
    database="stockradar"
)
db.autocommit = True

def save_to_db(category_name, store_name, product_name, product_url, in_stock, price):
    cursor = db.cursor()

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
    select_price_sql = "SELECT price_id FROM price WHERE stock_id = %s"
    cursor.execute(select_price_sql, (stock_id,))
    price_row = cursor.fetchone()

    price_int = int(price)

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

    cursor.close()

def job():
    print("\n=== job() 함수 실행 ===")

    with requests.Session() as session:
        for task in URL_TASKS:
            category_name = task["categoryName"]
            store_name = task["storeName"]
            product_url = task["url"]

            if store_name == "Amazon":
                product_name, in_stock, price = get_amazon_data(session, product_url)
            elif store_name == "SSG":
                product_name, in_stock, price = get_ssg_data(session, product_url)
            elif store_name == "11ST":
                product_name, in_stock, price = get_11st_data(session, product_url)
            else:
                product_name, in_stock, price = ("Unknown Product", False, 0)

            print(f"[{store_name}] 크롤링 결과 -> {product_name}, 재고: {in_stock}, 가격: {price}")
            save_to_db(
                category_name=category_name,
                store_name=store_name,
                product_name=product_name,
                product_url=product_url,
                in_stock=in_stock,
                price=price
            )

if __name__ == "__main__":
    print("Requests + BeautifulSoup + New Entity 크롤링 서버 시작!")
    # 즉시 실행
    job()
    # 5분 간격
    schedule.every(5).minutes.do(job)

    while True:
        schedule.run_pending()
        time.sleep(1)
