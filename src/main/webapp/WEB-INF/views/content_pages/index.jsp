<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- 지도 영역 -->
<div class="map-wrapper">
  <div class="map_container" id="mapContainer">
    <div class="mapview" id="vmap"></div>
    <button id="map-toggle-btn">지도 전체보기</button>
  </div>
</div>

<!-- 메인 콘텐츠 영역 -->
<main class="main">
  <div class="middle-section">
    <div class="background-section">
      <div class="yellow-bg"></div>
      <div class="navy-bg"></div>
    </div>

    <div class="top-section">
      <div class="yellow-top">
        <div class="weather-info" id="weather-app">
          <div class="weather-temp-sky-rain">
            <span class="weather-temp"></span>
            <div class="weather-sky"></div>
            <span class="weather-rain"></span>
          </div>
          <div class="weather-details">
            <span class="wds-default">미세</span>
            <span class="dust-value-0"></span>
            <span class="wds-default">초미세</span>
            <span class="dust-value-0"></span>
          </div>
        </div>
      </div>
      <div class="navy-top"></div>
    </div>
  </div>

  <!-- 콘텐츠 박스 3개 -->
  <div class="content-section">

    <!-- 공지사항 캐로우절 -->
    <div style="margin-bottom: 10px;">
      <h3 style="color:rgb(182, 28, 199); text-align:center; margin-bottom:5px;">공지사항</h3>
      <div class="content-box">
        <div id="notice-swiper-app">
          <div class="swiper" id="notice-swiper" style="width: 100%; max-width: 500px; position: relative;">
            <div class="swiper-wrapper">
              <div class="swiper-slide" v-for="notice in notices" :key="notice.id"
                   :style="getSlideStyle(notice)">
                <!-- 제목 -->
                <p style="color: black; font-weight: bold; margin: 0 0 8px; text-align: center;">
                  {{ notice.title }}
                </p>

                <!-- 이미지가 있으면 표시 -->
                <img v-if="notice.image" :src="notice.image" alt="공지 이미지"
                     style="width: 100%; max-height: 180px; object-fit: cover;" />

                <!-- 본문 내용이 있으면 표시 -->
                <div v-if="notice.content" v-html="notice.content"
                     style="color: black; font-size: 14px; line-height: 1.6; text-align: left; padding-top: 8px;"></div>
              </div>
            </div>

            <!-- Swiper 기본 네비게이션 버튼 -->
            <div class="swiper-button-prev"></div>
            <div class="swiper-button-next"></div>

            <!-- 재생/정지 토글 버튼 -->
            <button id="notice-swiper-toggle" class="swiper-toggle-btn">Pause</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 실시간 돌발상황 -->
    <div style="margin-bottom: 10px;">
      <h3 style="color:rgb(185, 101, 17); text-align:center; margin-bottom:5px;">실시간 돌발상황</h3>
      <div class="content-box">
        <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
      </div>
    </div>

    <!-- 서울 대기오염 정보 -->
    <div style="margin-bottom: 10px;">
      <h3 style="color:rgb(51, 176, 13); text-align:center; margin-bottom:5px;">서울 대기오염 정보</h3>
      <div class="content-box">
        <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
        <div class="air-box">
          <video class="air-bg-video" autoplay muted loop playsinline>
            <source src="/videos/AirQuality/sky.mp4" type="video/mp4" />
          </video>
          <div id="air-info-box"></div>
        </div>
      </div>
    </div>

  </div>

  <div class="ITS-link">
    <div class="ITS-link-box">
      <p>타 ITS 사이트 링크 걸 공간</p>
    </div>
  </div>
</main>

<!-- Vue + Swiper Composition Script -->
<script type="module">
  import { createApp, ref, onMounted } from 'vue'

  const App = {
    setup() {
      const notices = ref([
        {
          id: 1,
          type: '공지',
          title: '[공지] 6월 정기 시스템 점검 안내',
          content: `
            안녕하세요, 서울교통정보센터입니다.<br>
            보다 안정적인 서비스 제공을 위해 다음과 같이 시스템 점검을 진행합니다. 이용에 불편을 드려 죄송합니다. 감사합니다.<br><br>
        
            🛠 점검 일시: 2025년 6월 17일(화) 09:00 ~ 18:00<br>
            🔌 점검 내용: 서버 정기 보안 업데이트 및 트래픽 최적화<br>
            🚫 서비스 영향: 점검 시간 동안 지도/대중교통정보 일부 서비스 접속 불가<br><br>
          `
        },
        {
          id: 2,
          type: '안내',
          title: '[안내] 로그인 보안 강화 적용 예정',
          content: `
            보다 안전한 교통정보 이용을 위해 로그인 시 보안 인증 절차가 강화될 예정입니다.<br><br>
            🔐 적용 일자: 2025년 6월 25일(화)<br>
            📌 주요 변경: 비밀번호 변경 주기 도입, 2단계 인증 시범 적용<br><br>
            향후 정식 적용 전까지 관련 의견을 수렴하겠습니다.<br>
            감사합니다.
          `
        },
        {
          id: 3,
          type: '이벤트',
          title: '[이벤트] 대중교통 이용 캠페인',
          image: '/images/transport-campaign.png'
        }
      ])

      // 공지 유형에 따라 슬라이드 배경 스타일 지정
      const getSlideStyle = (notice) => {
        const base = {
          padding: '15px',
          borderRadius: '10px',
          height: 'auto',
          boxSizing: 'border-box'
        }

        if (notice.type === '공지') {
          return { ...base, backgroundColor: '#fffbe6' }
        } else if (notice.type === '안내') {
          return { ...base, backgroundColor: '#e3f2fd' }
        } else {
          return base // 이벤트(이미지)는 흰 배경 또는 투명
        }
      }

      onMounted(() => {
        // 1) Swiper 인스턴스 생성
        const swiper = new Swiper('#notice-swiper', {
          direction: 'horizontal',
          loop: true,
          autoplay: {
            delay: 7000,
            disableOnInteraction: false,  // 화살표 눌러도 자동재생 유지
            pauseOnMouseEnter: true        // 마우스 올리면 일시정지
          },
          navigation: {                    // 화살표 활성화
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev'
          }
        })

        // 2) 재생/정지 토글
        const toggleBtn = document.getElementById('notice-swiper-toggle')
        let isPlaying = true
        toggleBtn.addEventListener('click', () => {
          if (isPlaying) {
            swiper.autoplay.stop()
            toggleBtn.textContent = 'Play'
          } else {
            swiper.autoplay.start()
            toggleBtn.textContent = 'Pause'
          }
          isPlaying = !isPlaying
        })
      })

      return { notices, getSlideStyle }
    }
  }

  createApp(App).mount('#notice-swiper-app')
</script>
