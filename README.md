# 🚦 Seoul ITS Info - 서울 교통정보 통합 플랫폼

A-Team이 개발한 서울시 지능형 교통체계(ITS) 정보 제공 웹 플랫폼

## 📋 프로젝트 개요

Seoul ITS Info는 서울시민들에게 실시간 교통정보를 제공하는 종합 웹 플랫폼입니다. 교통 상황, 지하철, 주차장, CCTV, 날씨 정보 등을 한 곳에서 확인할 수 있으며, AI 챗봇을 통해 개인화된 교통정보 서비스를 제공합니다.

## ✨ 주요 기능

### 🗺️ 교통 정보
- 실시간 교통 상황: 서울시 도로 소통 정보 및 교통량 시각화
- 돌발 상황: 교통사고, 공사, 행사 등 실시간 돌발상황 알림
- 도로 CCTV: 주요 도로의 실시간 CCTV 영상 제공

### 🚇 대중교통
- 지하철 정보: 서울 지하철 노선도 및 실시간 운행정보
- 버스 정보: 서울시 버스 노선 및 정류장 정보
- 따릉이: 서울시 공공자전거 대여소 현황

### 🅿️ 주차장 정보
- 공영 주차장: 서울시 공영주차장 위치 및 실시간 현황
- 민영 주차장: 민영주차장 정보 및 요금 안내

### 🌤️ 날씨 & 대기질
- 실시간 날씨: 서울시 날씨 정보 및 예보
- 대기질: 미세먼지, 초미세먼지 등 대기질 정보

### 🤖 AI 챗봇
- 날씨 상담: Gemini AI를 활용한 날씨 정보 상담
- 데이터 분석: Ollama 기반 교통데이터 분석 및 인사이트 제공

### 💬 커뮤니티
- 게시판: 공지사항, 자유게시판 등 커뮤니티 기능
- 사용자 관리: 회원가입, 로그인, 프로필 관리

## 🛠️ 기술 스택

### Backend
- Framework: Spring Boot 3.4.6
- Language: Java 17
- Database: MySQL
- ORM: MyBatis
- Security: Spring Security
- API: REST API, WebFlux

### Frontend
- View: JSP, JSTL
- Styling: CSS3, Responsive Design
- JavaScript: Vanilla JS, Vue.js
- Maps: OpenLayers, Google Maps API, Kakao Maps API

### AI & External APIs
- AI Models: Google Gemini, Ollama (Gemma 3:4b)
- APIs: 서울시 열린데이터 광장, 공공데이터포털, 기상청 API, 카카오 API, 네이버 API, T-Map API

### Python Services
- Framework: FastAPI
- Libraries: BeautifulSoup4, GeoPandas, Requests
- Functions: 크롤링, 파일 서버, Ollama 연동, 날씨 데이터 처리

## 🚀 설치 및 실행

### 사전 요구사항
- Java 17+
- MySQL 8.0+
- Python 3.8+ (Python API 사용 시)
- Maven 3.6+

### 1. 저장소 클론

```bash
git clone https://github.com/ParkChangHyun7/ATEAMSECONDROUND.git
cd ATEAMSECONDROUND
```

### 2. 데이터베이스 설정

MySQL에서 스키마를 생성합니다.

```sql
CREATE DATABASE `seoul_its_info2`;
```

보안을 위해 `src/main/resources/application.properties`는 git에 포함되어 있지 않습니다. `src/main/resources/` 경로에 아래 내용으로 직접 파일을 생성해주세요.

```properties
spring.application.name=SeoulITSInfoByATeamApplication.java
server.port=9998
spring.profiles.active=dev

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/seoul_its_info2?useSSL=false&serverTimezone=Asia/Seoul
spring.datasource.username=본인의_MySQL_계정
spring.datasource.password=본인의_MySQL_비밀번호

mybatis.mapper-locations=classpath:src/main/resources/mapper/*.xml
mybatis.type-aliases-package=seoul.its.info
mybatis.configuration.map-underscore-to-camel-case=true
```

> ⚠️ **보안 안내**: 기존에 설정되어 있던 Google OAuth 클라이언트 시크릿은 저장소가 Public으로 전환되면서 노출된 이력이 있어 재발급/비활성화될 예정입니다. Google 로그인 기능을 테스트하려면 Google Cloud Console에서 별도의 OAuth 2.0 클라이언트를 새로 발급받아 설정해야 합니다.
>
> ⚠️ 구글 OAuth, 이메일 발송, 외부 API 키 등은 별도 설정이 필요하며, 해당 기능 없이도 기본 페이지 실행 및 확인은 가능합니다.
>
> 📌 개발 당시 사용하던 팀 공용 서버는 현재 운영 종료된 상태로, 위 방법을 통해 로컬 환경에서 동일하게 재현 가능합니다.

### 3. API 키 설정

`src/main/resources/com/properties/application-API-KEY.properties` 파일에 다음 API 키들을 설정하세요:

- Kakao API Key
- Google Maps API Key
- 공공데이터포털 API Key
- 기상청 API Key
- Gemini API Key
- 기타 필요한 API 키들

### 4. 애플리케이션 실행

```bash
# Spring Boot 애플리케이션 실행
./mvnw spring-boot:run

# 또는 JAR 파일 빌드 후 실행
./mvnw clean package
java -jar target/seoul-its-info-0.0.1-SNAPSHOT.jar
```

실행 후 `http://localhost:9998` 접속하면 확인 가능합니다.

### 5. Python API 실행 (선택사항)

```bash
cd python_api
pip install -r requirements.txt
uvicorn file_server:app --reload --port 8001
```

## 🔧 트러블슈팅

- **의존성 충돌 해결**: `antisamy` 라이브러리가 전이적으로 끌어오는 구버전 `xerces`/`xml-apis`가 Java 17에 내장된 `java.xml` 모듈과 충돌하여 `NoClassDefFoundError`가 발생. `pom.xml`에서 해당 의존성을 exclusion 처리하여 해결.

## 📁 프로젝트 구조

\`\`\`
ATEAMSECONDROUND/
├─ src/main/
│  ├─ java/seoul/its/info/
│  │  ├─ common/         # 공통 설정 및 유틸리티
│  │  ├─ services/       # 서비스 레이어
│  │  │  ├─ traffic/     # 교통 정보 서비스
│  │  │  ├─ metro/       # 지하철 정보 서비스
│  │  │  ├─ llm/         # AI 챗봇 서비스
│  │  │  ├─ boards/      # 게시판 서비스
│  │  │  └─ users/       # 사용자 관리 서비스
│  │  └─ main/           # 메인 컨트롤러
│  ├─ resources/
│  │  ├─ static/         # 정적 리소스 (CSS, JS, 이미지)
│  │  └─ data/           # 데이터 파일
│  └─ webapp/WEB-INF/views/  # JSP 뷰 파일
├─ python_api/           # Python 기반 API 서비스
└─ data/                 # 정적 데이터 파일 (JSON, CSV)
\`\`\`

## 🌐 주요 엔드포인트

- `/` - 메인 페이지
- `/traffic/trafficflowmap` - 교통 소통 정보
- `/traffic/eventMap` - 돌발 상황 지도
- `/traffic/cctvMap` - CCTV 지도
- `/parking` - 주차장 정보
- `/metro` - 지하철 정보
- `/chat` - AI 챗봇
- `/boards/{boardId}/posts` - 게시판

## 🔑 주요 API

### 교통 정보
- `GET /api/traffic/events` - 실시간 교통 이벤트
- `GET /api/traffic/flow` - 교통 소통 정보

### AI 챗봇
- `POST /api/classifier` - 질문 분류
- `POST /api/chat/handler` - 일반 채팅
- `GET /api/chat/dataAnalyze-stream` - 데이터 분석 (SSE)

### 주차장
- `GET /api/parking/public` - 공영 주차장 정보

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 LICENSE 파일을 참고하세요.

## 👥 팀 정보

A-Team - Seoul ITS Info 개발팀

## 📞 문의사항

프로젝트에 대한 문의사항이나 버그 리포트는 Issues를 통해 연락주세요.
chang6100@naver.com

---

**Seoul ITS Info** - 더 스마트한 서울 교통정보 플랫폼 🚀
