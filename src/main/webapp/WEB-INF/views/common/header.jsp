<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!-- 헤더 -->
  <header class="header_container" id="main-header-app">
    <div class="header-main">
      <div class="header-left">
        <div class="logo-container">
          <a href="/" class="logo-link">
            <span class="logo-img">
              <img src="/images/main_logo_small.png" alt="서울교통정보센터" />
            </span>
            <div class="site-name">
              <span class="site-name-seoul">서울특별시</span>
              <span class="site-name-tic">교통정보센터</span>
            </div>
            <div class="site-name-eng">
              <span class="site-name-tic">Seoul Traffic Information</span>
            </div>
          </a>
        </div>
      </div>
      <div class="header-right-wrap">
        <div class="nav-menu-wrap">
          <div class="nav_container">
            <nav class="navs">
              <ul>
                <li><a href="/about">소개</a></li>

                <!--  주차장 드롭다운 -->
                <li class="dropdown">
                  <a href="#">교통정보보</a>
                  <ul class="dropdown-menu">
                    <li><a href="/parking">공영주차장</a></li>
                    <li><a href="/private-parking">민영주차장</a></li>
                    <li><a href="/traffic/cctvMap">CCTV</a></li>
                    <li><a href="/traffic/eventMap">돌발정보</a></li>
                    <li><a href="/traffic/trafficflowmap">소통정보</a></li>
                  </ul>
                </li>

                <li><a href="/contact">문의하기</a></li>
                <li><a href="/boards/1/posts">커뮤니티</a></li>
              </ul>
              <span class="material-symbols-outlined">
                brightness_4
              </span>
              <span class="material-symbols-outlined" style="color: white; font-size: 40px;"
                @click="toggleMenu">menu_open</span>
            </nav>
          </div>
        </div>
      </div>
    </div>
    <div id="dropdown-menu-overlay"></div>
    <div class="auth-wrap">
      <div class="auth-links" id="auth-links-app"></div>
    </div>
  </header>