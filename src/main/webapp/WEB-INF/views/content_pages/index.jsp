<!--  index.jsp 전체 (JSP + 비디오 영역 포함) -->
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
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>

    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>

    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
      <div class="air-box">
        <video class="air-bg-video" autoplay muted loop playsinline>
          <source src="/videos/AirQuality/sky.mp4" type="video/mp4" />
          브라우저가 video 태그를 지원하지 않습니다.
        </video>
        <!-- Vue가 mount 될 영역 -->
        <div id="air-info-box"></div>
      </div>
    </div>
  </div>

  <div class="ITS-link">
    <div class="ITS-link-box">
      <p>타 ITS 사이트 링크 걸 공간</p>
    </div>
  </div>
</main>
