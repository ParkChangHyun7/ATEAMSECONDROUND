# 🚦 Seoul ITS Info - 서울시 교통정보 통합 플랫폼

> **A-Team이 개발한 서울시 지능형 교통 정보 시스템**  
> 실시간 교통정보, 지하철 정보, 주차장 현황, CCTV 등 서울시 교통 관련 다양한 공공데이터를 통합하여 제공하는 웹 플랫폼

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Python](https://img.shields.io/badge/Python-3.x-3776AB?style=flat-square&logo=python)](https://www.python.org/)

## 📖 프로젝트 소개

Seoul ITS Info는 서울시의 다양한 교통 관련 공공데이터를 활용하여 시민들에게 통합된 교통정보를 제공하는 웹 플랫폼입니다. 
실시간 교통 상황, 지하철 운행 정보, 주차장 현황, CCTV 영상 등을 한 곳에서 확인할 수 있으며, AI 챗봇을 통한 교통정보 문의 서비스도 제공합니다.

### 🎯 개발 목적
- 분산된 서울시 교통정보를 하나의 플랫폼에서 통합 제공
- 시민들의 교통편의성 향상 및 이동 효율성 증대
- 공공데이터 활용을 통한 사회적 가치 창출
- 최신 웹 기술 스택을 활용한 사용자 친화적 서비스 구현

## ✨ 주요 기능

### 🗺️ 교통정보 시각화
- **실시간 교통상황 지도**: 서울시 전체 도로의 실시간 교통 흐름 정보
- **교통사고 현황**: 실시간 교통사고 및 도로 상황 정보
- **CCTV 영상**: 주요 교차로 및 도로의 실시간 CCTV 영상 제공

### 🚇 지하철 정보
- **실시간 지하철 운행정보**: 각 역별 실시간 도착 정보
- **지하철 사고 통계**: 최근 5년간 지하철 사고 현황 분석
- **노선도 및 경로 안내**: 대화형 지하철 노선도

### 🅿️ 주차장 정보
- **실시간 주차장 현황**: 공영/민영 주차장의 실시간 주차 가능 대수
- **주차장 위치 지도**: 지도 기반 주차장 검색 및 안내
- **요금 정보**: 주차장별 요금 체계 안내

### 🌤️ 날씨 및 대기질
- **실시간 날씨 정보**: 기상청 API 연동 날씨 정보
- **대기질 지수**: 서울시 각 구별 실시간 대기질 정보
- **날씨 기반 교통 예보**: 날씨 조건을 고려한 교통상황 예측

### 🤖 AI 챗봇
- **Gemini API 연동**: Google Gemini를 활용한 교통정보 문의 서비스
- **자연어 처리**: 사용자 질문에 대한 맞춤형 교통정보 제공
- **실시간 데이터 연동**: 최신 교통정보를 바탕으로 한 답변 제공

### 👥 사용자 시스템
- **회원가입/로그인**: Spring Security 기반 인증 시스템
- **게시판**: 교통정보 공유 및 커뮤니티 기능
- **문의하기**: 사용자 피드백 및 문의 처리 시스템

## 🛠️ 기술 스택

### Backend
- **Framework**: Spring Boot 3.4.6
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: MyBatis 3.0.4
- **Security**: Spring Security
- **Build Tool**: Maven

### Frontend
- **Template Engine**: JSP, JSTL
- **CSS Framework**: Custom CSS with Responsive Design
- **JavaScript**: Vanilla JS, jQuery 3.7.1
- **Maps**: Google Maps API, OpenLayers
- **Charts**: Custom Visualization

### External APIs
- **교통정보**: 서울시 교통정보 API, ITS API
- **지하철**: 서울교통공사 API
- **주차장**: 서울시 주차장 정보 API
- **날씨**: 기상청 API
- **지도**: Google Maps API, T-Map API
- **AI**: Google Gemini API

### Python Services
- **크롤링**: 실시간 데이터 수집
- **파일 서버**: 이미지 및 파일 관리
- **Ollama 연동**: 로컬 AI 모델 서비스

## 📁 프로젝트 구조

```
src/main/
├── java/seoul/its/info/
│   ├── services/          # 비즈니스 로직
│   │   ├── traffic/       # 교통정보 서비스
│   │   ├── metro/         # 지하철 정보 서비스
│   │   ├── llm/          # AI 챗봇 서비스
│   │   └── users/        # 사용자 관리 서비스
│   ├── common/           # 공통 모듈
│   │   ├── config/       # 설정 클래스
│   │   ├── security/     # 보안 설정
│   │   └── exception/    # 예외 처리
│   └── main/            # 메인 컨트롤러
├── resources/
│   ├── static/          # 정적 리소스 (CSS, JS, Images)
│   ├── data/            # 공공데이터 및 상수
│   └── application.properties
└── webapp/WEB-INF/views/ # JSP 뷰 파일
```

## 🚀 설치 및 실행

### 사전 요구사항
- Java 17 이상
- MySQL 8.0 이상
- Maven 3.6 이상
- Python 3.x (부가 서비스용)

### 설치 방법

1. **프로젝트 클론**
```bash
git clone https://github.com/ParkChangHyun7/seoul-its-info.git
cd seoul-its-info
```

2. **데이터베이스 설정**
```sql
CREATE DATABASE seoul_its_info;
-- 테이블 스키마는 src/main/resources/schema.sql 참조
```

3. **API 키 설정**
```properties
# src/main/resources/com/properties/application-API-KEY.properties
# 각 공공데이터 포털에서 API 키 발급 후 설정
seoul.default.api.key=your_api_key
google.map=your_google_maps_key
gemini.api.key=your_gemini_key
```

4. **애플리케이션 실행**
```bash
mvn clean install
mvn spring-boot:run
```

5. **브라우저에서 접속**
```
http://localhost:9998
```

## 📊 주요 성과 및 학습 포인트

### 📈 기술적 성과
- **15개 이상의 공공 API 통합**: 다양한 데이터 소스를 효율적으로 관리
- **실시간 데이터 처리**: 대용량 교통 데이터의 실시간 처리 및 시각화
- **반응형 웹 디자인**: 모바일/태블릿/데스크톱 환경 최적화
- **AI 서비스 연동**: Gemini API를 활용한 자연어 기반 교통정보 서비스

### 🎓 학습 포인트
- **마이크로서비스 아키텍처**: 서비스별 모듈화 설계 경험
- **공공데이터 활용**: 다양한 공공 API 연동 및 데이터 정제 경험
- **실시간 웹 서비스**: WebSocket, 비동기 처리를 통한 실시간 서비스 구현
- **보안 강화**: Spring Security를 활용한 인증/인가 시스템 구축
- **성능 최적화**: 대용량 데이터 처리 및 캐싱 전략 적용

## 🔮 향후 계획

- [ ] **모바일 앱 개발**: React Native 기반 모바일 애플리케이션
- [ ] **AI 기능 강화**: 교통 패턴 분석 및 예측 서비스
- [ ] **개인화 서비스**: 사용자 맞춤형 교통정보 추천
- [ ] **실시간 알림**: Push 알림을 통한 교통정보 제공
- [ ] **다국어 지원**: 외국인 관광객을 위한 다국어 서비스

## 👥 개발팀

**A-Team** - 서울시 교통정보 시스템 개발팀

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 아래 연락처로 연락해 주세요.

- **Email**: [chang6100@naver.com]
- **GitHub**: [https://github.com/ParkChangHyun7]
- **Portfolio**: [https://your-portfolio.com]

---

⭐ **이 프로젝트가 도움이 되었다면 Star를 눌러주세요!**
