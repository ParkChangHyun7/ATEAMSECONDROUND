<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!-- 지도 들어갈 자리 -->
    <div class="map_container">
        <div class="mapview" id="vmap"></div>
    </div>
    <!-- 메인 섹션 -->
    <main class="main">
        <!-- 중앙 섹션 (5:5 바와 3:7 바 겹침) -->
        <div class="middle-section">
            <!-- 5:5 비율 노란/파란 바 (배경) -->
            <div class="background-section">
                <div class="yellow-bg"></div>
                <div class="navy-bg"></div>
            </div>
            <!-- 3:7 비율 노란/파란 바 (상단) -->
            <div class="top-section">
                <div class="yellow-top">
                    <!-- 날씨 정보 -->
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

        <!-- 하단 콘텐츠 박스 -->
        <div class="content-section">
            <div class="content-box">
                <div class="index-box"> <span>●</span>
                    <span>●</span>
                    <span>●</span>
                </div>
            </div>
            <div class="content-box">
                <div class="index-box"> <span>●</span>
                    <span>●</span>
                    <span>●</span>
                </div>
            </div>
            <div class="content-box">
                <div class="index-box"> <span>●</span>
                    <span>●</span>
                    <span>●</span>
                </div>
            </div>
        </div>
        <div class="ITS-link">
            <div class="ITS-link-box">
                <p>타 ITS 사이트 링크 걸 공간</p>
            </div>
        </div>
    </main>