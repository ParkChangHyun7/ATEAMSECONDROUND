<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

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
    <!-- 배경 색상 레이어 (노란/남색 배경 바) -->
    <div class="background-section">
      <div class="yellow-bg"></div>
      <div class="navy-bg"></div>
    </div>

    <!-- 실제 콘텐츠 바 (날씨 정보 포함) -->
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

  <!-- 하단 박스 콘텐츠 영역 -->
  <div class="content-section">
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>
    <div class="content-box">
      <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
    </div>
  </div>

  <!-- ITS 링크 -->
  <div class="ITS-link">
    <div class="ITS-link-box">
      <p>타 ITS 사이트 링크 걸 공간</p>
    </div>
  </div>
</main>
