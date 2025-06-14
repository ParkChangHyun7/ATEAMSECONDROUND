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
          <div class="swiper" id="notice-swiper" style="width: 100%; max-width: 500px;">
            <div class="swiper-wrapper">
             <div class="swiper-slide" v-for="notice in notices" :key="notice.id" style="text-align: center;">
                <p style="
                  color: black;
                  font-weight: bold;
                  margin: 0;
                  padding: 10px;
                  width: 100%;
                  display: block;
                  background-color: white;
                  text-align: center;
                ">
                  {{ notice.title }}
                </p>
              <img v-if="notice.image" :src="notice.image" alt="공지 이미지"
                  style="width: 100%; max-height: 180px; object-fit: cover; border-radius: 8px;" />
              </div>
            </div>
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
        { id: 1, title: '[공지] 6월 정기 점검 안내' },
        { id: 2, title: '[안내] 주차장 정기 점검' },
        {
          id: 3,
          title: '[이벤트] 대중교통 이용 캠페인',
          image: '/images/transport-campaign.png' // ✅ 이미지 경로
        }
      ])

      onMounted(() => {
        new Swiper('#notice-swiper', {
          direction: 'horizontal',
          loop: true,
          autoplay: {
            delay: 3000,
          },
        })
      })

      return { notices }
    }
  }

  createApp(App).mount('#notice-swiper-app')
</script>
