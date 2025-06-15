import requests
from bs4 import BeautifulSoup
from fastapi import FastAPI, HTTPException
from datetime import datetime
import json
import os

app = FastAPI()

@app.get("/weather/{location}")
async def get_weather(location: str):
    """
    지정된 지역의 현재 날씨 정보를 크롤링하여 반환합니다.
    """
    naver_weather_url = f"https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query={location}+날씨"
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36'
    }

    try:
        response = requests.get(naver_weather_url, headers=headers)
        response.raise_for_status()  # HTTP 오류 발생 시 예외 발생
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] 웹 페이지 가져오기 실패: {e}")
        raise HTTPException(status_code=500, detail=f"웹 페이지를 가져오는 중 오류 발생: {e}")

    soup = BeautifulSoup(response.text, 'html.parser')
    
    weather_data = {}
    
    # 1. 현재 날씨 정보 추출
    try:
        # 현재 온도
        current_temp_element = soup.select_one('div.temperature_text strong')
        if current_temp_element:
            weather_data["current_temperature"] = current_temp_element.get_text(strip=True).replace('현재 온도', '')

        # 체감 온도, 습도, 바람
        summary_list = soup.select('dl.summary_list dd')
        if len(summary_list) >= 3:
            weather_data["sensible_temperature"] = summary_list[0].get_text(strip=True)
            weather_data["humidity"] = summary_list[1].get_text(strip=True)
            weather_data["wind"] = summary_list[2].get_text(strip=True)

        # 미세먼지, 초미세먼지, 자외선, 일출/일몰
        today_info_elements = soup.select('ul.today_info li.item_today_info')
        if not today_info_elements:
            print("[DEBUG] today_info_elements not found.")
        for item in today_info_elements:
            txt_element = item.select_one('span.txt')
            state_element = item.select_one('span.state')
            if txt_element and state_element:
                info_type = txt_element.get_text(strip=True)
                info_state = state_element.get_text(strip=True)
                
                if "미세먼지" in info_type:
                    weather_data["fine_dust"] = info_state
                elif "초미세먼지" in info_type:
                    weather_data["ultrafine_dust"] = info_state
                elif "자외선" in info_type:
                    weather_data["uv_index"] = info_state
            
            # 일출/일몰은 strong 태그로 바로 접근 후 다음 형제 노드(시각 정보) 가져오기
            sunrise_sunset_strong = item.select_one('strong')
            if sunrise_sunset_strong and ("일출" in sunrise_sunset_strong.get_text() or "일몰" in sunrise_sunset_strong.get_text()):
                time_element = sunrise_sunset_strong.next_sibling
                if time_element:
                    if "일출" in sunrise_sunset_strong.get_text():
                        weather_data["sunrise"] = time_element.strip()
                    elif "일몰" in sunrise_sunset_strong.get_text():
                        weather_data["sunset"] = time_element.strip()
        
    except Exception as e:
        print(f"[ERROR] 현재 날씨 정보 파싱 중 오류 발생: {e}") 

    # 2. 시간별 예보 추출 (사용자 요청으로 제외)
    # hourly_forecast = []
    # hourly_list_elements = soup.select('div.today_chart ul.chart_list li') 
    # if not hourly_list_elements:
    #     print("[DEBUG] hourly_list_elements not found.")
    
    # for item in hourly_list_elements:
    #     time_element = item.select_one('dt em') # 시간 (e.g., "02시")
    #     weather_condition_element = item.select_one('dd') # 날씨 상태는 dd 태그의 첫 번째 텍스트 노드
    #     temp_element = item.select_one('dd span.num') # 온도 (e.g., "21°")
        
    #     hour_data = {}
    #     if time_element:
    #         hour_data["time"] = time_element.get_text(strip=True)
        
    #     if weather_condition_element:
    #         condition_text = "".join([str(content) for content in weather_condition_element.contents if isinstance(content, str)]).strip()
    #         if condition_text:
    #              hour_data["condition"] = condition_text
    #         else:
    #             icon_title = weather_condition_element.select_one('i.ico_weather')
    #             if icon_title and icon_title.get('title'):
    #                 hour_data["condition"] = icon_title.get('title')

    #     if temp_element:
    #         hour_data["temperature"] = temp_element.get_text(strip=True)
            
    #     if hour_data:
    #         hourly_forecast.append(hour_data)
    
    # if hourly_forecast:
    #     weather_data["hourly_forecast"] = hourly_forecast
    # else:
    #     print("[DEBUG] No hourly forecast data extracted.")
        
    # 3. 주간 예보 추출
    weekly_forecast = []
    # 대장님 제공 HTML 스냅샷 기반으로 선택자 수정: div.list_box._weekly_weather ul.week_list li
    weekly_list_elements = soup.select('div.list_box._weekly_weather ul.week_list li.week_item') 
    if not weekly_list_elements:
        print("[DEBUG] weekly_list_elements not found. (Revised selector: div.list_box._weekly_weather ul.week_list li.week_item)")

    for item in weekly_list_elements:
        day_data = {}
        
        # 날짜와 요일은 div.cell_date span.date_inner 안에 있음
        date_info_container = item.select_one('div.cell_date span.date_inner')
        if date_info_container:
            # 요일 (strong.day 태그)
            day_name_element = date_info_container.select_one('strong.day')
            if day_name_element:
                day_data["day_name"] = day_name_element.get_text(strip=True)
            
            # 날짜 (span.date 태그)
            date_element = date_info_container.select_one('span.date')
            if date_element:
                day_data["date"] = date_element.get_text(strip=True)

        # 최저/최고 온도 (span.lowest, span.highest 태그에서 텍스트 직접 추출)
        temp_min_element = item.select_one('span.lowest') 
        temp_max_element = item.select_one('span.highest') 
        
        if temp_min_element:
            day_data["min_temperature"] = temp_min_element.get_text(strip=True)
        if temp_max_element:
            day_data["max_temperature"] = temp_max_element.get_text(strip=True)
            
        # 오전/오후 날씨 및 강수확률
        # div.cell_weather 안에 두 개의 span.weather_inner 가 오전/오후 정보를 담음
        weather_inner_elements = item.select('div.cell_weather span.weather_inner')
        
        # 첫 번째 span.weather_inner는 오전, 두 번째는 오후 정보
        if len(weather_inner_elements) > 0:
            am_info_container = weather_inner_elements[0]
            am_time_element = am_info_container.select_one('strong.time') # 오전/오후 표시는 time 클래스로 따로 가져와서 처리함.
            am_rain_prob_element = am_info_container.select_one('span.rainfall')
            am_condition_text_el = am_info_container.select_one('i.wt_icon span.blind') # blind 클래스에서 개요 추출

            if am_time_element:
                day_data["am_time"] = am_time_element.get_text(strip=True)
            if am_rain_prob_element:
                day_data["am_rain_probability"] = am_rain_prob_element.get_text(strip=True)
            if am_condition_text_el:
                day_data["am_condition"] = am_condition_text_el.get_text(strip=True)
            
        if len(weather_inner_elements) > 1:
            pm_info_container = weather_inner_elements[1]
            pm_time_element = pm_info_container.select_one('strong.time') # 오전/오후 표시는 time 클래스로 따로 가져와서 처리함.
            pm_rain_prob_element = pm_info_container.select_one('span.rainfall')
            pm_condition_text_el = pm_info_container.select_one('i.wt_icon span.blind') # blind 클래스에서 개요 추출

            if pm_time_element:
                day_data["pm_time"] = pm_time_element.get_text(strip=True)
            if pm_rain_prob_element:
                day_data["pm_rain_probability"] = pm_rain_prob_element.get_text(strip=True)
            if pm_condition_text_el:
                day_data["pm_condition"] = pm_condition_text_el.get_text(strip=True)
                    
        if day_data:
            weekly_forecast.append(day_data)
            
    if weekly_forecast:
        weather_data["weekly_forecast"] = weekly_forecast
    else:
        print("[DEBUG] No weekly forecast data extracted.")

    # JSON 파일로 저장 (1시간 단위 저장은 스케줄러 필요, 여기서는 요청 시마다 저장)
    current_time = datetime.now().strftime("%Y%m%d_%H%M%S")
    file_path = f"weather_data_{location}_{current_time}.json"
    
    # weather_data_storage 폴더 생성 (없으면)
    storage_dir = "weather_data_storage"
    os.makedirs(storage_dir, exist_ok=True)
    full_file_path = os.path.join(storage_dir, file_path)

    with open(full_file_path, "w", encoding="utf-8") as f:
        json.dump(weather_data, f, ensure_ascii=False, indent=4)
    
    print(f"날씨 정보가 {full_file_path}에 저장되었습니다.")

    return weather_data

# 이 애플리케이션을 실행하려면 터미널에서 다음 명령어를 사용하세요:
# uvicorn weather:app --host 0.0.0.0 --port 8000