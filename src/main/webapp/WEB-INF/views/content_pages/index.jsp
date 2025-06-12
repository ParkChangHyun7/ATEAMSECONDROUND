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
    <!-- 배경 색상 레이어 -->
    <div class="background-section">
      <div class="yellow-bg"></div>
      <div class="navy-bg"></div>
    </div>

    <!-- 상단 날씨 바 -->
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

  <!-- 3개 박스 영역 -->
  <div class="content-section">
    <!-- 공지사항 -->
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>

    <!-- 실시간 돌발정보 -->
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>

    <!-- 대기오염 정보 (여기에 Vue가 mount됨) -->
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
      <div id="air-info-box"></div>
    </div>
  </div>

  <!-- ITS 링크 -->
  <div class="ITS-link">
    <div class="ITS-link-box">
      <p>타 ITS 사이트 링크 걸 공간</p>
    </div>
  </div>
</main>
