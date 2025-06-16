<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!-- 지도 들어갈 자리 -->
    <div class="map_container">
        <div class="speed-meter">
            <span></span>
            <span></span></div>
        <div class="mapview" id="vmap"></div>
    </div>
    <!-- 메인 섹션 -->
    <main class="main" id="app">
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
                <div class="navy-top" >
                    <div class="sitemap-icons">
                    <a href="/parking">
                        <div class="middle-icons parking">
                            <span class="material-symbols-outlined">
                                local_parking
                            </span>
                            <span class="icon-text">주차장 정보</span>
                        </div>
                    </a>
                    <a href="/traffic/trafficflowmap">
                        <div class="middle-icons traffic">
                            <span class="material-symbols-outlined">
                                traffic_jam
                            </span>
                            <span class="icon-text">소통 정보</span>
                        </div>
                    </a>
                    <a href="/traffic/eventMap">
                        <div class="middle-icons event">
                            <span class="material-symbols-outlined">
                                sync_problem
                            </span>
                            <span class="icon-text">돌발 정보</span>
                        </div>
                    </a>
                    <a href="/traffic/cctvMap">
                        <div class="middle-icons cctv">
                            <span class="material-symbols-outlined">
                                speed_camera
                            </span>
                            <span class="icon-text">도로 CCTV</span>
                        </div>
                        </a>
                        <div class="middle-icons metro">
                            <span class="material-symbols-outlined">
                                train
                            </span>
                            <span class="icon-text">서울 지하철</span>
                        </div>
                        <div class="middle-icons bus">
                            <span class="material-symbols-outlined">
                                directions_bus
                            </span>
                            <span class="icon-text">서울 버스</span>
                        </div>
                        <div class="middle-icons bicycle">
                            <span class="material-symbols-outlined">
                                pedal_bike
                            </span>
                            <span class="icon-text">따릉이</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 하단 콘텐츠 박스 -->
        <div class="content-section">
            <div class="content-box">
                <div class="index-box"><span>●</span>
                    <span>●</span>
                    <span>●</span>
                </div>
                <div class="board-content">
                    <div class="content-title 1"><span class="ctt">공지사항</span><a href="/boards/3/posts"><span class="material-symbols-outlined"
                            style="color: black; font-size: 60px;">
                            read_more
                        </span></a></div>
                    <div class="board-content-content" style="overflow-y: hidden;">
                        <ul class="notice-list">
                            <li v-for="notice in noticePosts" :key="notice.id">
                                <a :href="'/boards/' + notice.boardId + '/posts/read/' + notice.id">
                                    {{ notice.title }}
                                </a>
                            </li>
                            <li v-if="noticePosts.length === 0">
                                <p>게시된 공지사항이 없습니다.</p>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="content-box">
                <div class="index-box"> <span>●</span>
                    <span>●</span>
                    <span>●</span>
                </div>
                <div class="board-content">
                    <div class="content-title 2">
                        <span class="ctt">주요 교통 정보</span>
                        <div class="traffic-controls">
                            <button class="event-arrow backward" @click="showPrevEvent">
                                <span class="material-symbols-outlined">arrow_back_ios_new</span>
                            </button>
                            <button class="event-arrow forward" @click="showNextEvent">
                                <span class="material-symbols-outlined">arrow_forward_ios</span>
                            </button>
                        </div>
                    </div>
                    <div class="board-content-content">
                        <div v-html="currentEventHtml" class="traffic-event-display"></div>
                    </div>
                </div>
            </div>
            <div class="content-box">
                <div class="index-box"> <span>●</span>
                    <span>●</span>
                    <span>●</span>
                </div>
                <div class="board-content">
                    <div class="content-title 3"><span class="ctt">서울 교통 뉴스</span><span
                            class="material-symbols-outlined" style="color: black; font-size: 60px;">
                            read_more
                        </span></div>
                    <div class="board-content-content">
                        <ul class="news-list">
                            <li v-for="news in naverNews" :key="news.link">
                                <a :href="news.link" target="_blank">
                                    {{ news.title }}
                                </a>
                            </li>
                            <li v-if="naverNews.length === 0">
                                <p>표시할 교통 뉴스가 없습니다.</p>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="ITS-link">
            <div class="ITS-link-box">
                <div class="logo-box" style="display: flex; gap: 40px;">
                    <img src="/images/ext_logos/seoulcity.png" style="width: auto; height: 40px;" alt="서울시 로고">
                    <img src="/images/ext_logos/seoulopendata.png" style="width: auto; height: 40px;" alt="서울시 로고">
                    <img src="/images/ext_logos/weatheradminis.png" style="width: auto; height: 40px;" alt="서울시 로고">
                    <img src="/images/ext_logos/chatgpt.png" style="width: auto; height: 40px;" alt="서울시 로고">
                    <img src="/images/ext_logos/gemini.png" style="width: auto; height: 40px;" alt="서울시 로고">
            </div>
        </div>
    </main>