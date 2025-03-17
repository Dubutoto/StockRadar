# coroutine.py
import asyncio
import time
import aiomysql
import aiohttp
import logging
import os
from logging.handlers import TimedRotatingFileHandler
from crawler2 import process_task
from url_config import URL_TASKS

# 로그 디렉토리 생성
LOG_DIR = "logs"
if not os.path.exists(LOG_DIR):
    os.makedirs(LOG_DIR)

# 로그 설정 (터미널 + 파일 저장)
log_formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s", datefmt="%Y-%m-%d %H:%M:%S")
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# 터미널 출력 핸들러
console_handler = logging.StreamHandler()
console_handler.setFormatter(log_formatter)
logger.addHandler(console_handler)

# 로그 파일 핸들러 (2일마다 자동 삭제)
file_handler = TimedRotatingFileHandler(os.path.join(LOG_DIR, "crawler.log"), when="D", interval=2, backupCount=1, encoding="utf-8")
file_handler.setFormatter(log_formatter)
logger.addHandler(file_handler)

# 비동기 DB 연결 및 적재 함수
async def save_to_db(pool, category_name, store_name, product_name, product_url, in_stock, price):
    async with pool.acquire() as conn:
        async with conn.cursor() as cursor:
            try:
                # 1) category
                await cursor.execute("SELECT category_id FROM category WHERE category_name = %s", (category_name,))
                cat_row = await cursor.fetchone()
                if cat_row:
                    category_id = cat_row[0]
                else:
                    await cursor.execute("INSERT INTO category (category_name) VALUES (%s)", (category_name,))
                    category_id = cursor.lastrowid

                # 2) store
                await cursor.execute("SELECT store_id FROM store WHERE store_name = %s", (store_name,))
                store_row = await cursor.fetchone()
                if store_row:
                    store_id = store_row[0]
                else:
                    await cursor.execute("INSERT INTO store (store_name) VALUES (%s)", (store_name,))
                    store_id = cursor.lastrowid

                # 3) product
                await cursor.execute("SELECT product_id FROM product WHERE product_url = %s", (product_url,))
                prod_row = await cursor.fetchone()
                if prod_row:
                    product_id = prod_row[0]
                    await cursor.execute(
                        "UPDATE product SET product_name = %s, category_id = %s, store_id = %s WHERE product_id = %s",
                        (product_name, category_id, store_id, product_id)
                    )
                else:
                    await cursor.execute(
                        "INSERT INTO product (product_name, product_url, category_id, store_id) VALUES (%s, %s, %s, %s)",
                        (product_name, product_url, category_id, store_id)
                    )
                    product_id = cursor.lastrowid

                # 4) stock_status
                availability_value = 1 if in_stock else 0
                await cursor.execute("SELECT stock_id FROM stock_status WHERE product_id = %s", (product_id,))
                stock_row = await cursor.fetchone()
                if stock_row:
                    stock_id = stock_row[0]
                    await cursor.execute(
                        "UPDATE stock_status SET availability = %s, last_updated = NOW() WHERE stock_id = %s",
                        (availability_value, stock_id)
                    )
                else:
                    await cursor.execute(
                        "INSERT INTO stock_status (availability, last_updated, product_id) VALUES (%s, NOW(), %s)",
                        (availability_value, product_id)
                    )
                    stock_id = cursor.lastrowid

                # 5) price
                await cursor.execute("SELECT price_id FROM price WHERE stock_id = %s", (stock_id,))
                price_row = await cursor.fetchone()
                price_int = int(price)
                if price_row:
                    price_id = price_row[0]
                    await cursor.execute(
                        "UPDATE price SET price = %s, last_update = NOW() WHERE price_id = %s",
                        (price_int, price_id)
                    )
                else:
                    await cursor.execute(
                        "INSERT INTO price (price, last_update, stock_id) VALUES (%s, NOW(), %s)",
                        (price_int, stock_id)
                    )
            except Exception as e:
                logger.error(f"DB 저장 중 오류 발생: {e}")

async def main():
    # DB 커넥션 풀 생성 (aiomysql)
    logger.info("MySQL 연결 풀 생성 중...")
    pool = await aiomysql.create_pool(
        host="localhost",
        port=3306,
        user="root",
        password="Dubutoto22!",
        db="stockradar",
        autocommit=True,
        minsize=1,
        maxsize=5
    )

    async with aiohttp.ClientSession() as session:
        logger.info("크롤링 시작!")
        tasks = [process_task(session, task) for task in URL_TASKS]
        start_time = time.perf_counter()
        results = await asyncio.gather(*tasks, return_exceptions=True)
        end_time = time.perf_counter()
        elapsed_time = end_time - start_time

        total = len(results)
        success_count = sum(1 for res in results if not isinstance(res, Exception))
        fail_count = total - success_count

        logger.info(f"총 작업: {total} 건, 성공: {success_count} 건, 실패: {fail_count} 건, 크롤링 소요 시간: {elapsed_time:.2f} 초")

        # DB 저장: 각 URL_TASKS의 정보와 크롤링 결과를 매핑하여 저장
        for task, res in zip(URL_TASKS, results):
            if not isinstance(res, Exception):
                category_name = task["categoryName"]
                store_name = task["storeName"]
                product_url = task["url"]
                product_name = res[1]
                in_stock = res[2]
                price_value = res[3]
                await save_to_db(pool, category_name, store_name, product_name, product_url, in_stock, price_value)
    
    pool.close()
    await pool.wait_closed()
    logger.info("크롤링 종료!")

if __name__ == '__main__':
    asyncio.run(main())
