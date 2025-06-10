<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="subway-accident-app" data-accidents='<%= request.getAttribute("accidents") %>'>
    <h2>지하철 출입문 사고 현황(최근 5년)</h2>
    <canvas id ="accidentChart"></canvas>
</div>