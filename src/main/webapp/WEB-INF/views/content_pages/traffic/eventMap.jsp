<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<div class="event-container">
	  <div class="sidebar">
	    <h3>⚠️ 돌발상황 목록</h3>

	    <div id="filterButtons">
	      <button class="filter-btn full" data-type="all">전체</button>
	      <button class="filter-btn" data-type="공사">공사</button>
	      <button class="filter-btn" data-type="교통사고">교통사고</button>
	      <button class="filter-btn" data-type="기상">기상</button>
	      <button class="filter-btn" data-type="기타돌발">기타돌발</button>
	      <button class="filter-btn" data-type="재난">재난</button>
	      <button class="filter-btn" data-type="기타">기타</button>
	    </div>

	    <ul id="eventList"></ul>
	  </div>

	  <div id="map" class="map"></div>
	</div>




</body>
</html>