<%@ page contentType="text/html; charset=UTF-8" %>

<div class="parking-container">
    <h2>서울시 공영주차장 지도</h2>
    
    <!-- 검색 및 필터 컨트롤 영역 -->
    <div class="search-filter-container">
        <!-- 검색바 -->
        <div class="search-section">
            <div class="search-box">
                <input type="text" id="searchInput" placeholder="주차장 이름 또는 주소 검색..." />
                <button id="searchBtn" class="search-btn">
                    <span class="material-symbols-outlined">search</span>
                </button>
                <button id="clearBtn" class="clear-btn">
                    <span class="material-symbols-outlined">clear</span>
                </button>
            </div>
        </div>

        <!-- 필터링 옵션들 -->
        <div class="filter-section">
            <!-- 구별 필터 -->
            <div class="filter-group">
                <label for="districtFilter">자치구:</label>
                <select id="districtFilter">
                    <option value="">전체 구</option>
                    <option value="강남구">강남구</option>
                    <option value="강동구">강동구</option>
                    <option value="강북구">강북구</option>
                    <option value="강서구">강서구</option>
                    <option value="관악구">관악구</option>
                    <option value="광진구">광진구</option>
                    <option value="구로구">구로구</option>
                    <option value="금천구">금천구</option>
                    <option value="노원구">노원구</option>
                    <option value="도봉구">도봉구</option>
                    <option value="동대문구">동대문구</option>
                    <option value="동작구">동작구</option>
                    <option value="마포구">마포구</option>
                    <option value="서대문구">서대문구</option>
                    <option value="서초구">서초구</option>
                    <option value="성동구">성동구</option>
                    <option value="성북구">성북구</option>
                    <option value="송파구">송파구</option>
                    <option value="양천구">양천구</option>
                    <option value="영등포구">영등포구</option>
                    <option value="용산구">용산구</option>
                    <option value="은평구">은평구</option>
                    <option value="종로구">종로구</option>
                    <option value="중구">중구</option>
                    <option value="중랑구">중랑구</option>
                </select>
            </div>

            <!-- 요금별 필터 -->
            <div class="filter-group">
                <label for="feeFilter">요금:</label>
                <select id="feeFilter">
                    <option value="">전체</option>
                    <option value="free">무료</option>
                    <option value="paid">유료</option>
                    <option value="low">저렴 (시간당 1000원 이하)</option>
                    <option value="medium">보통 (시간당 1000-2000원)</option>
                    <option value="high">비싼 (시간당 2000원 초과)</option>
                </select>
            </div>

            <!-- 주차장 타입 필터 -->
            <div class="filter-group">
                <label for="typeFilter">타입:</label>
                <select id="typeFilter">
                    <option value="">전체 타입</option>
                    <option value="normal">일반 주차장</option>
                    <option value="bus">버스 전용</option>
                    <option value="public">공영</option>
                    <option value="private">민영</option>
                </select>
            </div>

            <!-- 운영시간 필터 -->
            <div class="filter-group">
                <label for="timeFilter">운영시간:</label>
                <select id="timeFilter">
                    <option value="">전체 시간</option>
                    <option value="24h">24시간</option>
                    <option value="day">주간 (06:00-22:00)</option>
                    <option value="extended">연장 (06:00-24:00)</option>
                    <option value="night">야간 가능</option>
                </select>
            </div>

            <!-- 필터 리셋 버튼 -->
            <div class="filter-group">
                <button id="resetFiltersBtn" class="reset-btn">
                    <span class="material-symbols-outlined">refresh</span>
                    필터 초기화
                </button>
            </div>
        </div>

        <!-- 검색 결과 카운터 -->
        <div class="result-info">
            <span id="resultCount">전체 주차장을 표시하고 있습니다.</span>
        </div>
    </div>

    <!-- 지도 표시 영역 -->
    <div id="map"></div>
</div>