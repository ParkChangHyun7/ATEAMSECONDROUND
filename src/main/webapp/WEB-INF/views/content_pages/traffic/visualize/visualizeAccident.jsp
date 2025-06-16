<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- <body> 태그 및 상위 태그와 스타일은 레이아웃 파일에서 미리 선언 됨 --%>
<div id="visualizeApp">
    <div class="filters">
        <div class="filter-group">
            <label for="yearSelect">연도 선택:</label>
            <select id="yearSelect" v-model="selectedYear">
                <option value="2020">2020</option>
                <option value="2021">2021</option>
                <option value="2022">2022</option>
                <option value="2023">2023</option>
                <option value="2024">2024</option>
            </select>
        </div>
        <div class="filter-group">
            <label>대상사고 구분명:</label>
            <div class="checkbox-group">
                <label><input type="checkbox" value="전체" v-model="selectedAccidentTypes"> 전체</label>
                <label><input type="checkbox" value="PM사고" v-model="selectedAccidentTypes"> PM사고</label>
                <label><input type="checkbox" value="고령보행사고" v-model="selectedAccidentTypes"> 고령보행사고</label>
                <label><input type="checkbox" value="고령운전사고" v-model="selectedAccidentTypes"> 고령운전사고</label>
                <label><input type="checkbox" value="노인사고" v-model="selectedAccidentTypes"> 노인사고</label>
                <label><input type="checkbox" value="무면허사고" v-model="selectedAccidentTypes"> 무면허사고</label>
                <label><input type="checkbox" value="보행자사고" v-model="selectedAccidentTypes"> 보행자사고</label>
                <label><input type="checkbox" value="뺑소니사고" v-model="selectedAccidentTypes"> 뺑소니사고</label>
                <label><input type="checkbox" value="스쿨존내어린이사고" v-model="selectedAccidentTypes"> 스쿨존내어린이사고</label>
                <label><input type="checkbox" value="야간사고" v-model="selectedAccidentTypes"> 야간사고</label>
                <label><input type="checkbox" value="어린이보행사고" v-model="selectedAccidentTypes"> 어린이보행사고</label>
                <label><input type="checkbox" value="어린이사고" v-model="selectedAccidentTypes"> 어린이사고</label>
                <label><input type="checkbox" value="자전거사고" v-model="selectedAccidentTypes"> 자전거사고</label>
            </div>
        </div>
        <div class="filter-group">
            <label for="chartTypeSelect">시각화 방식:</label>
            <select id="chartTypeSelect" v-model="selectedChartType">
                <option value="bar">막대 그래프</option>
                <option value="line">꺾은선 그래프</option>
                <option value="pie">원 그래프</option>
            </select>
        </div>
    </div>
    <div class="chart-container">
        <canvas id="accidentChart"></canvas>
    </div>
</div>
<%-- <script></body>, vue cdn, importmap 등은 레이아웃 파일에서 미리 선언 됨 --%>