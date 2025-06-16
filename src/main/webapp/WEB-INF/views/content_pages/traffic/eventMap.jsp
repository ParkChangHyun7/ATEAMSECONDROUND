<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/traffic/eventMap/resources.jsp" %>


<div class="event-container">
  <div class="sidebar">
    <h3>⚠️ 돌발상황 목록</h3>

    <!-- 검색창 -->
    <input type="text" id="searchInput" placeholder="도로명 또는 메시지 검색"
      style="width: 100%; margin-bottom: 10px; padding: 5px;" />

    <!-- 필터 버튼 -->
    <div id="filterButtons">
      <button class="filter-btn full" data-type="all">전체</button>
      <button class="filter-btn" data-type="공사">공사</button>
      <button class="filter-btn" data-type="교통사고">교통사고</button>
      <button class="filter-btn" data-type="기상">기상</button>
      <button class="filter-btn" data-type="기타돌발">기타돌발</button>
      <button class="filter-btn" data-type="재난">재난</button>
      <button class="filter-btn" data-type="기타">기타</button>
    </div>

    <!-- 돌발상황 목록을 표 형태로 표시 -->
    <div id="eventListWrapper">
      <div class="table-container">
        <table id="eventTable" class="event-table">
          <thead>
            <tr>
              <th>구분</th>
              <th>도로명</th>
              <th>상황</th>
            </tr>
          </thead>
          <tbody id="eventList">
            <!-- 동적으로 생성되는 돌발상황 목록 -->
          </tbody>
        </table>
      </div>
    </div>
    <button id="scrollToTopBtn">🔝 TOP</button>
  </div>

  <div id="map" class="map"></div>
</div>
