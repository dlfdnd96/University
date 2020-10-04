# coding: utf-8

"""
네이버 날씨에서 강수 확률과 시간을 crawling

author: 류일웅
since: 2019-10-03
"""
from bs4 import BeautifulSoup as bs

import requests
import pymysql
import time

# 네이버 날씨 URL을 가져옴
html = requests.get('https://search.naver.com/search.naver?query=날씨')
soup = bs(html.text, 'html.parser')

# 강수량, 풍량, 습도 class
weather_info_array = ('info_list weather_condition _tabContent', 'info_list humidity _tabContent')

"""
날씨 정보를 가져오는 기능

@author: 류일웅
@since: 19.10.09
@param: 
@return: 
"""
def get_all_info_weather():
    # 날씨 정보 값을 담을 리스트
    weather_data_list = list()

    # 시간 값을 담을 리스트
    time_data_list = list()

    # 현재 시간
    current_time = time.strftime('%y-%m-%d')

    for i in range(2):
        # 각 클래스의 전체 내용
        whole_data = soup.find('div', {'class': weather_info_array[i]})

        # 전체 내용을 배열로 저장
        array_content_data = whole_data.find('ul', {'class': 'list_area'})

        # 각 클래스에 해당하는 부분만 배열로 저장
        content = array_content_data.find_all('dd', {'class': 'weather_item _dotWrapper'})

        weather_data_list.append(handle_all_weather_data(content, 0, 8))

    time_data_list.append(handle_time_data())

    weather_data_tuple = []

    # 시간별로 날씨값들을 튜플로 저장
    for i in range(0, 8):
        weather_data_tuple.insert(
            i, (
                current_time,
                time_data_list[0][i],
                weather_data_list[0][i],
                weather_data_list[1][i],
            ))

    # 날씨 값을 데이터베이스에 저장
    execute_sql_query_mysql(weather_data_tuple)


"""
전체 날씨 정보값을 가져오는 기능

@author: 류일웅
@since: 19.10.09
@param: content, start_num, end_num
@return: find_weather_data
"""
def handle_all_weather_data(content, start_num, end_num):
    find_weather_data = list()

    for num in range(start_num, end_num):
        find_weather_data.append(content[num].find('span', {'class': None}).text)

    return find_weather_data


"""
시간 값을 가져오는 기능

@author: 류일웅
@since: 19.10.09
@param: 
@return: time
"""
def handle_time_data():
    whole_data = soup.find('div', {'class': weather_info_array[0]})
    array_content_data = whole_data.find('ul', {'class': 'list_area'})
    time_contents = array_content_data.find_all('dd', {'class': 'item_time'})

    find_time = time_contents[0].find('span', {'class': "dday"}).text
    naver_time = list()

    naver_time.append(find_time)

    for i in range(1, 8):
        find_time = time_contents[i].find('span', {'class': None}).text
        naver_time.append(find_time)

    return naver_time


"""
데이터베이스 제어

@author: 류일웅
@since: 19.10.23
@param: data
@return: 
"""
def execute_sql_query_mysql(data):
    # MySQL Connection 연결
    conn = pymysql.connect(host='localhost', user='root', password='abcd1234',
                           db='mobile_adaptation_programming', charset='utf8')
    try:
        # Connection 으로부터 Cursor 생성
        with conn.cursor() as curs:
            weather_data = data

            # SQL문 실행
            truncate_sql = """TRUNCATE TABLE weather_info;"""
            insert_sql = """INSERT INTO weather_info (date, hour, temperature, humidity) 
                        VALUES (%s, %s, %s, %s)"""

            # Truncate 수행
            curs.execute(truncate_sql)

            # Insert 수행
            curs.executemany(insert_sql, weather_data)

        conn.commit()

    finally:
        # Connection 닫기
        conn.close()


if __name__ == "__main__":
    print("=======", time.ctime(), "=======")

    get_all_info_weather()
