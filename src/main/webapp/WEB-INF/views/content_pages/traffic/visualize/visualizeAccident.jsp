<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%-- <body> 태그 및 상위 태그와 스타일은 레이아웃 파일에서 미리 선언 됨 --%>
            <div class="topdiv-visualize">
                <div class="visualize-container" id="visualizeApp" v-cloak>
                    <h1 class="visualize-title">교통사고 현황 시각화</h1>
                    
                    <div class="filters">
                        <div class="filter-group">
                            <label>대상사고 구분명:</label>
                            <div class="checkbox-group">
                                <label><input type="checkbox" value="전체" v-model="selectedAccidentTypes"> 전체</label>
                                <label><input type="checkbox" value="PM사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> PM사고</label>
                                <label><input type="checkbox" value="고령보행사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    고령보행사고</label>
                                <label><input type="checkbox" value="고령운전사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    고령운전사고</label>
                                <label><input type="checkbox" value="노인사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 노인사고</label>
                                <label><input type="checkbox" value="무면허사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    무면허사고</label>
                                <label><input type="checkbox" value="보행자사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    보행자사고</label>
                                <label><input type="checkbox" value="뺑소니사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    뺑소니사고</label>
                                <label><input type="checkbox" value="스쿨존내어린이사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    스쿨존내어린이사고</label>
                                <label><input type="checkbox" value="야간사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 야간사고</label>
                                <label><input type="checkbox" value="어린이보행사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    어린이보행사고</label>
                                <label><input type="checkbox" value="어린이사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    어린이사고</label>
                                <label><input type="checkbox" value="자전거사고" v-model="selectedAccidentTypes" :disabled="selectedChartType === 'pie'"> 
                                    자전거사고</label>
                            </div>
                        </div>
                        <div class="filter-group">
                            <label>사고 및 사상자수:</label>
                            <div class="checkbox-group">
                                <label><input type="checkbox" value="사고건수" v-model="selectedCasualtyMetrics" :disabled="selectedChartType === 'pie'"> 사고건수</label>
                                <label><input type="checkbox" value="사망자수" v-model="selectedCasualtyMetrics" :disabled="selectedChartType === 'pie'"> 사망자수</label>
                                <label><input type="checkbox" value="부상자수" v-model="selectedCasualtyMetrics" :disabled="selectedChartType === 'pie'"> 부상자수</label>
                            </div>
                        </div>
                        <div class="filter-group">
                            <label>연도 선택:</label>
                            <div class="checkbox-group">
                                <label><input type="checkbox" value="2020" v-model="selectedYearsCheckbox" :disabled="selectedChartType === 'line'"> 2020</label>
                                <label><input type="checkbox" value="2021" v-model="selectedYearsCheckbox" :disabled="selectedChartType === 'line'"> 2021</label>
                                <label><input type="checkbox" value="2022" v-model="selectedYearsCheckbox" :disabled="selectedChartType === 'line'"> 2022</label>
                                <label><input type="checkbox" value="2023" v-model="selectedYearsCheckbox" :disabled="selectedChartType === 'line'"> 2023</label>
                                <label><input type="checkbox" value="2024" v-model="selectedYearsCheckbox" :disabled="selectedChartType === 'line'"> 2024</label>
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
                    
                    <%-- 데이터가 로드된 후에만 그래프와 채팅 섹션을 렌더링합니다. --%>
                    <div v-if="isDataLoaded">
                        <div class="chart-container">
                            <canvas id="accidentChart"></canvas>
                        </div>
                
                        <div class="chat-section">
                            <h2 class="chat-title">데이터 분석 질문하기</h2>
                            <div class="chat-messages" ref="chatMessagesContainer">
                                <div v-for="(msg, index) in chatMessages" :key="index" class="message-container" :class="{ 'user-message-container': msg.type === 'user', 'ai-message-container': msg.type === 'ai' }">
                                    <div class="message" :class="msg.type">
                                        <p class="message-text" v-html="msg.text.replace(/\n/g, '<br>')"></p>
                                        <span class="timestamp">{{ formatTime(msg.timestamp) }}</span>
                                    </div>
                                </div>
                                <div v-if="isLoading" class="message-container ai-message-container">
                                    <div class="message ai">
                                        <p class="message-text loading-indicator">
                                            <span>답변을 생성 중입니다</span><span>.</span><span>.</span><span>.</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <div class="chat-input">
                                <textarea v-model="userQuestion" @keydown.enter.prevent="sendQuestion" placeholder="질문을 입력하세요..."></textarea>
                                <button @click="sendQuestion" :disabled="isLoading">전송</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%-- <script></body>, vue cdn, importmap 등은 레이아웃 파일에서 미리 선언 됨 --%>