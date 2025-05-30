<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	<div class="box">
		<div id="search">
			<h1>지하철역 정보</h1>
			<div class="searchBox">
				<input type="text" class="searchInput" placeholder="지하철역을 입력하세요" />
				<button class="searchBtn">검색하기</button>
			</div>
			<div class="resultNameBox">
			</div>
		</div>
		<div id="result">
			<div id="resultStation"></div> <!-- 지하철역명 + 호선 -->
			<div id="resultHistory"></div> <!-- 지하철역명 유래 -->
			<div id="resultMapName"></div>
			<div id="resultMap" style="width:70%;height:550px;"></div>
			<div id="resultInfo"></div> <!-- 지하철역 기본정보 넣는곳 -->
			<div id="resultAmenities"></div> <!-- 지하철역 기본정보 넣는곳 -->
				
			
		</div>	
	
	
	
	</div>
	<!-- 모달 -->
    <div class="modal-overlay" id="modal">
        <div class="modal-content">
        	<!-- 이미지 넣기 -->
        </div>
    </div>