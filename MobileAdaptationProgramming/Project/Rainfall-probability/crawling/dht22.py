# coding: utf-8

"""
DHT22에서 측정값을 데이터베이스에 저장

author: 류일웅
since: 2019-11-09
"""
import pymysql
import time
from time import sleep
import Adafruit_DHT

# Set DHT and data pin
DHT_SENSOR = Adafruit_DHT.DHT22
DHT_PIN = 4

limit_number = 0


def DHT22_data():
    # Reading from DHT22 and storing the temperature and humidity
    humidity, temperature = Adafruit_DHT.read_retry(DHT_SENSOR, DHT_PIN)

    return humidity, temperature


"""
데이터베이스 제어

@author: 류일웅
@since: 19.10.23
@param: data
@return: 
"""
def insert_data(datas, limit):
    count = limit
    # MySQL Connection 연결
    conn = pymysql.connect(host='localhost', user='root', password='abcd1234',
                           db='mobile_adaptation_programming', charset='utf8')
    try:
        # Connection 으로부터 Cursor 생성
        with conn.cursor() as curs:
            weather_data = datas

            # SQL문 실행
            insert_sql = """INSERT INTO dht22_info (datetime, temperature, humidity) 
                        VALUES (%s, %s, %s)"""

            # Insert 수행
            curs.execute(insert_sql, weather_data)

            count += 1

        conn.commit()

        if count >= 10:
            with conn.cursor() as curs:
                truncate_sql = """TRUNCATE TABLE dht22_info;"""

                curs.execute(truncate_sql)

                count = 0

    finally:
        # Connection 닫기
        conn.close()

    return count


if __name__ == "__main__":
    while True:
        try:
            data = []

            humi, temp = DHT22_data()

            # If Reading is valid
            if isinstance(humi, float) and isinstance(temp, float):
                # Formatting to two decimal places
                humi = '%.2f' % humi
                temp = '%.2f' % temp

                current_time = time.strftime('%y-%m-%d %H:%m:%S')

                data.insert(0, current_time)
                data.insert(1, temp)
                data.insert(2, humi)

                limit_number = insert_data(data, limit_number)
            else:
                print("Error")

            # DHT22 requires 2 seconds to give a reading, so make sure to add delay of above 2 seconds.
            sleep(5)

        except Exception as ex:
            print("Except occur: ", ex)
            break

