<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- 본문 시작 -->
<div class="cctv-container">
  <!-- ✅ CCTV 목록 사이드바 -->
  <div class="sidebar">
    <h3>📷 CCTV 목록</h3>

    <input type="text" id="cctvSearchInput" placeholder="CCTV 이름 검색" style="width:100%; margin-bottom:10px; padding:5px;" />

    <!-- ✅ 필터 버튼 -->
    <div id="filterButtons" style="margin-bottom: 10px;">
      <button class="filter-btn" data-type="all">전체</button>
      <button class="filter-btn" data-type="ex">고속도로</button>
      <button class="filter-btn" data-type="its">국도</button>
    </div>

    <!-- ✅ 리스트 -->
    <div id="cctvListWrapper">
      <ul id="cctvList"></ul>
    </div>

    <!-- ✅ TOP 버튼 (바깥으로 분리) -->
    <button id="scrollToTopBtn">🔝 TOP</button>
  </div>

  <!-- ✅ 지도 영역 -->
  <div id="map" class="map"></div>
</div>

<!-- ✅ 검색창 -->
<div style="position: absolute; top: 10px; left: 50%; transform: translateX(-50%); z-index: 10;">
  <input type="text" id="searchKeyword" placeholder="장소 검색..." style="padding: 5px; width: 200px;" />
  <button id="searchButton">검색</button>
</div>

