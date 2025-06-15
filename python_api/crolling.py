from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import requests
from bs4 import BeautifulSoup

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:9998"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/news/naver")
async def get_naver_news():
    """
    네이버 뉴스 검색 결과 페이지에서 서울 도로 교통 관련 뉴스 정보를 크롤링하여 반환합니다.
    """
    naver_news_url = "https://search.naver.com/search.naver?sm=tab_hty.top&where=news&ssc=tab.news.all&query=%22%EC%84%9C%EC%9A%B8%22+%EB%8F%84%EB%A1%9C+%EA%B5%90%ED%86%B5&oquery=%EC%84%9C%EC%9A%B8+%EB%8F%84%EB%A1%9C+%EA%B5%90%ED%86%B5"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }

    try:
        response = requests.get(naver_news_url, headers=headers, timeout=10)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, 'html.parser')
        
        results = []
        processed_links = set()

        news_containers = soup.select('div[class*="sds-comps-vertical-layout"][class*="sds-comps-full-layout"]') 

        for container in news_containers:
            img_tag = container.select_one('a img[alt$="의 이미지"]')
            
            if img_tag:
                alt_text = img_tag.get('alt')
                temp_soup = BeautifulSoup(alt_text, 'html.parser')
                for mark_tag in temp_soup.find_all('mark'):
                    mark_tag.unwrap()
                title = temp_soup.get_text(strip=True).replace('의 이미지', '').strip()

                link_tag = img_tag.find_parent('a')
                if link_tag and 'href' in link_tag.attrs:
                    link = link_tag['href']
                    
                    if title and title != '제목 없음' and link != '링크 없음' and \
                       "media.naver.com/press" not in link and link not in processed_links:
                        results.append({
                            'title': title,
                            'link': link
                        })
                        processed_links.add(link)
                    
                        if len(results) >= 8:
                            break

        if not results:
            raise HTTPException(status_code=404, detail="뉴스 데이터를 찾을 수 없습니다. 웹사이트 구조가 변경되었을 수 있습니다.")

        return results

    except requests.exceptions.Timeout:
        raise HTTPException(status_code=504, detail="네이버 뉴스 서버 응답 시간 초과")
    except requests.exceptions.ConnectionError:
        raise HTTPException(status_code=503, detail="네이버 뉴스 서버에 연결할 수 없습니다.")
    except requests.exceptions.RequestException as e:
        raise HTTPException(status_code=500, detail=f"네트워크 요청 중 오류 발생: {e} (FastAPI 오류)")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"데이터 파싱 중 오류 발생: {e} (FastAPI 내부 오류)")
