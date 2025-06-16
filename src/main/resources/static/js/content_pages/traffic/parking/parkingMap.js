// 전역 변수 선언
let map;
let markers = [];
let allParkingData = [];

// 페이지 로드 시 실행
window.addEventListener('load', function() {
  console.log("페이지 로드 완료");
  initializeMap();
});

// 지도 초기화
async function initializeMap() {
  console.log("지도 초기화 시작");
  
  // Kakao Maps API 확인
  if (typeof kakao === "undefined" || !kakao.maps) {
    console.error("Kakao Maps API가 로드되지 않았습니다.");
    setTimeout(initializeMap, 1000); // 1초 후 재시도
    return;
  }

  // 지도 컨테이너 확인
      const container = document.getElementById("map");
  if (!container) {
    console.error("지도 컨테이너를 찾을 수 없습니다.");
    return;
  }

  try {
    // 지도 생성
    const options = {
      center: new kakao.maps.LatLng(37.5665, 126.9780),
      level: 7
    };

    map = new kakao.maps.Map(container, options);
    console.log("지도 생성 성공");

    // 지도 컨트롤 추가
    const zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

    // 주차장 데이터 로드
    await loadParkingData();
    
    // 이벤트 리스너 등록
    setupEventListeners();
    
  } catch (error) {
    console.error("지도 초기화 실패:", error);
  }
}

// 주차장 데이터 로드
async function loadParkingData() {
  console.log("주차장 데이터 로드 시작");
  
  try {
    const response = await fetch("/api/parking");
    console.log("API 응답 상태:", response.status);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const text = await response.text();
    console.log("API 응답 받음, 길이:", text.length);
    
    const data = JSON.parse(text);
    const parkingList = data?.GetParkInfo?.row || [];
    
    console.log("파싱된 주차장 수:", parkingList.length);
    
    if (parkingList.length > 0) {
      console.log("첫 번째 주차장 데이터:", parkingList[0]);
      
      // 버스 관련 주차장 찾기
      let busCount = 0;
      let busParking = [];
      
      parkingList.forEach((parking, index) => {
        const name = parking.PKLT_NM || '';
        const type = parking.PKLT_KND_NM || '';
        const allText = name + ' ' + type;
        
        if (allText.includes('버스') || allText.includes('BUS') || allText.includes('bus')) {
          busCount++;
          if (busParking.length < 5) { // 처음 5개만 저장
            busParking.push({
              name,
              type,
              allText,
              index
            });
          }
        }
      });
      
      console.log("=== 버스 주차장 분석 ===");
      console.log("버스 관련 주차장 총 개수:", busCount);
      console.log("버스 주차장 샘플들:", busParking);
      
      // 모든 주차장 타입 분석
      const types = {};
      parkingList.forEach(parking => {
        const type = parking.PKLT_KND_NM || '기타';
        types[type] = (types[type] || 0) + 1;
      });
      console.log("주차장 타입별 분포:", types);
    }
    
    allParkingData = parkingList;
    displayAllMarkers();
    updateResultCount(allParkingData.length);
    
  } catch (error) {
    console.error("데이터 로드 실패:", error);
    showErrorMessage("주차장 데이터를 불러오는데 실패했습니다.");
  }
}

// 모든 마커 표시
function displayAllMarkers() {
  console.log("전체 마커 표시 시작");
  clearMarkers();
  
  // 첫 번째 주차장 데이터의 모든 필드 출력
  if (allParkingData.length > 0) {
    console.log("=== 첫 번째 주차장 데이터의 모든 필드 ===");
    const firstParking = allParkingData[0];
    Object.keys(firstParking).forEach(key => {
      console.log(`${key}: ${firstParking[key]}`);
    });
    console.log("=== 사용 가능한 모든 필드 목록 ===");
    console.log(Object.keys(firstParking).join(', '));
    console.log("=====================================");
  }
  
  let busCount = 0;
  let normalCount = 0;
  
  allParkingData.forEach(parking => {
    const lat = parseFloat(parking.LAT);
    const lng = parseFloat(parking.LOT);
    
    if (!isNaN(lat) && !isNaN(lng)) {
      const marker = createMarker(parking, lat, lng);
      if (marker) {
        markers.push(marker);
        if (marker.isBusMarker) {
          busCount++;
        } else {
          normalCount++;
        }
      }
    }
  });
  
  console.log(`마커 표시 완료: 총 ${markers.length}개 (일반: ${normalCount}개, 버스: ${busCount}개)`);
}

// 개별 마커 생성
function createMarker(parking, lat, lng) {
  try {
    const position = new kakao.maps.LatLng(lat, lng);
    
    // 버스 전용 주차장 체크 - 더 포괄적으로
    const name = parking.PKLT_NM || '';
    const type = parking.PKLT_KND_NM || '';
    const operator = parking.OPRP_NM || '';
    const management = parking.MNMT_NM || '';
    
    const allText = (name + ' ' + type + ' ' + operator + ' ' + management).toLowerCase();
    const isBus = allText.includes('버스') || 
                 allText.includes('bus') || 
                 allText.includes('버스전용') ||
                 allText.includes('시내버스') ||
                 allText.includes('버스터미널') ||
                 type.includes('버스');
    
    // 첫 10개 주차장에 대해 상세 로그
    const parkingIndex = allParkingData.indexOf(parking);
    if (parkingIndex < 10) {
      console.log(`[${parkingIndex}] 주차장:`, {
        name,
        type,
        operator,
        management,
        allText: allText.substring(0, 50),
        isBus
      });
    }
    
    const defaultImageUrl = isBus 
      ? "/images/parking/bus-parking.png" 
      : "/images/parking/parking-lot.png";
      
    const hoverImageUrl = isBus 
      ? "/images/parking/bus-parking-hover.png" 
      : "/images/parking/parking-lot-hover.png";

    const defaultImageSize = new kakao.maps.Size(32, 32);
    const hoverImageSize = new kakao.maps.Size(48, 48);

    const defaultMarkerImage = new kakao.maps.MarkerImage(
      defaultImageUrl, 
      defaultImageSize,
      {
        offset: new kakao.maps.Point(16, 32),
      }
    );

    const hoverMarkerImage = new kakao.maps.MarkerImage(
      hoverImageUrl, 
      hoverImageSize,
      {
        offset: new kakao.maps.Point(24, 48),
      }
    );
    
    const marker = new kakao.maps.Marker({
      position: position,
      map: map,
      image: defaultMarkerImage
    });
    
    // 호버 효과 이벤트 추가
    kakao.maps.event.addListener(marker, "mouseover", function () {
      marker.setImage(hoverMarkerImage);
    });

    kakao.maps.event.addListener(marker, "mouseout", function () {
      marker.setImage(defaultMarkerImage);
    });
    
    // 마커 클릭 이벤트 추가 (정보창 표시)
    kakao.maps.event.addListener(marker, 'click', function() {
      // 정보창 내용 생성
      const content = createInfoWindowContent(parking);
      
      // 기존 정보창이 있으면 닫기
      if (window.currentInfoWindow) {
        window.currentInfoWindow.close();
      }
      
      // 새 정보창 생성
      const infoWindow = new kakao.maps.InfoWindow({
        content: content,
        removable: true
      });
      
      // 정보창 열기
      infoWindow.open(map, marker);
      
      // 현재 정보창 저장
      window.currentInfoWindow = infoWindow;
    });
    
    // 마커에 데이터 저장
    marker.parkingData = parking;
    
    // 버스 마커인지 확인
    marker.isBusMarker = isBus;
    
    return marker;
    
  } catch (error) {
    console.error("마커 생성 실패:", error);
    return null;
  }
}

// 정보창 내용 생성 함수
function createInfoWindowContent(parking) {
  const name = parking.PKLT_NM || '정보 없음';
  const addr = parking.ADDR || '주소 정보 없음';
  const capacity = parking.TPKCT || '정보 없음';
  const tel = parking.TELNO || '정보 없음';
  
  // 요금 정보 - 더 상세하게
  const basicCharge = parking.PRK_CRG || '0';
  const addCharge = parking.ADD_CRG || '';
  const freeInfo = parking.CHGD_FREE_NM || '';
  const basicChargeHour = parking.PRK_CRG_HR || '';
  const basicChargeTime = parking.PRK_CRG_TM || '';
  const addChargeInfo = parking.ADD_CRG_INFO || '';
  const addChargeUnit = parking.ADD_CRG_TM || '';
  
  // 운영시간 정보 - 평일/주말 구분
  const weekdayStart = parking.WD_OPER_BGNG_TM || '';
  const weekdayEnd = parking.WD_OPER_END_TM || '';
  const weekendStart = parking.WE_OPER_BGNG_TM || '';
  const weekendEnd = parking.WE_OPER_END_TM || '';
  const holidayStart = parking.HLD_OPER_BGNG_TM || '';
  const holidayEnd = parking.HLD_OPER_END_TM || '';
  const operTimeInfo = parking.OPER_TM_NM || '';
  
  // 기타 정보
  const management = parking.MNMT_NM || '';
  const operator = parking.OPRP_NM || '';
  const parkingType = parking.PKLT_KND_NM || '';
  
  // 시간 포맷팅 함수 (0900 -> 09:00, 930 -> 09:30)
  function formatTime(timeStr) {
    if (!timeStr) return '';
    
    // 숫자만 추출
    const digits = timeStr.replace(/\D/g, '');
    if (digits.length < 3) return timeStr;
    
    let hours, minutes;
    if (digits.length === 3) {
      // 930 형태
      hours = digits.substring(0, 1);
      minutes = digits.substring(1, 3);
    } else if (digits.length >= 4) {
      // 0930, 1430 형태
      hours = digits.substring(0, digits.length - 2);
      minutes = digits.substring(digits.length - 2);
    }
    
    // 시간과 분을 2자리로 맞춤
    hours = hours.padStart(2, '0');
    minutes = minutes.padStart(2, '0');
    
    return `${hours}:${minutes}`;
  }
  
  // 요금 정보 포맷팅
  let chargeText = '';
  if (parseInt(basicCharge) === 0 || freeInfo.includes('무료')) {
    chargeText = '<span style="color:#27ae60;font-weight:bold;">무료</span>';
  } else {
    chargeText = `<span style="color:#e74c3c;font-weight:bold;">${basicCharge}원</span>`;
    
    // 기본요금 단위 결정 (우선순위: PRK_CRG_TM > PRK_CRG_HR > 기본값)
    if (basicChargeTime) {
      chargeText += `/${basicChargeTime}`;
    } else if (basicChargeHour) {
      chargeText += `/${basicChargeHour}`;
    } else {
      // 일반적인 주차장 기본요금 단위 적용
      const chargeAmount = parseInt(basicCharge);
      if (chargeAmount <= 500) {
        chargeText += '/10분';
      } else if (chargeAmount <= 1000) {
        chargeText += '/15분';
      } else if (chargeAmount <= 2000) {
        chargeText += '/30분';
      } else {
        chargeText += '/시간';
      }
    }
  }
  
  // 추가요금 정보 (단위 포함)
  let addChargeText = '';
  if (addCharge && addCharge !== '0') {
    addChargeText = `<span style="color:#f39c12;">${addCharge}원</span>`;
    if (addChargeUnit) {
      addChargeText += `/${addChargeUnit}`;
    } else if (addChargeInfo) {
      addChargeText += `/${addChargeInfo}`;
    } else {
      // 일반적인 주차장 추가요금 단위 적용
      const addChargeAmount = parseInt(addCharge);
      if (addChargeAmount <= 300) {
        addChargeText += '/5분';
      } else if (addChargeAmount <= 600) {
        addChargeText += '/10분';
      } else if (addChargeAmount <= 1000) {
        addChargeText += '/15분';
      } else {
        addChargeText += '/30분';
      }
    }
  } else {
    addChargeText = '정보 없음';
  }
  
  // 운영시간 포맷팅
  let weekdayTimeText = '';
  if (weekdayStart && weekdayEnd) {
    weekdayTimeText = `${formatTime(weekdayStart)} ~ ${formatTime(weekdayEnd)}`;
  } else if (operTimeInfo) {
    weekdayTimeText = operTimeInfo;
  } else {
    weekdayTimeText = '정보 없음';
  }
  
  let weekendTimeText = '';
  if (weekendStart && weekendEnd) {
    weekendTimeText = `${formatTime(weekendStart)} ~ ${formatTime(weekendEnd)}`;
  } else {
    weekendTimeText = weekdayTimeText; // 주말 정보가 없으면 평일과 동일
  }
  
  let holidayTimeText = '';
  if (holidayStart && holidayEnd) {
    holidayTimeText = `${formatTime(holidayStart)} ~ ${formatTime(holidayEnd)}`;
  } else {
    holidayTimeText = weekendTimeText; // 공휴일 정보가 없으면 주말과 동일
  }
  
  // 타입 및 운영자 정보
  let typeText = '';
  if (parkingType) {
    typeText = parkingType;
  }
  if (operator) {
    typeText += typeText ? ` (${operator})` : operator;
  }
  if (!typeText) {
    typeText = '정보 없음';
  }
  
  return `
    <div style="width:320px;padding:15px;font-family:'맑은 고딕',sans-serif;">
      <h3 style="margin:0 0 12px 0;color:#2c5aa0;font-size:16px;font-weight:bold;">${name}</h3>
      <table style="width:100%;border-collapse:collapse;font-size:13px;">
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;width:90px;">주소</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${addr}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">주차면수</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${capacity === '정보 없음' ? '정보 없음' : capacity + '면'}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">타입</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${typeText}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">기본요금</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${chargeText}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">추가요금</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${addChargeText}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">평일운영</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${weekdayTimeText}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">주말운영</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${weekendTimeText}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">공휴일운영</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${holidayTimeText}</td>
        </tr>
        <tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">전화번호</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${tel}</td>
        </tr>
        ${management ? `<tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">관리기관</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${management}</td>
        </tr>` : ''}
        ${freeInfo ? `<tr>
          <td style="padding:6px 8px;background:#f8f9fa;border:1px solid #ddd;font-weight:bold;">요금정보</td>
          <td style="padding:6px 8px;border:1px solid #ddd;">${freeInfo}</td>
        </tr>` : ''}
      </table>
    </div>
  `;
}

// 기존 마커 제거
function clearMarkers() {
  markers.forEach(marker => marker.setMap(null));
  markers = [];
}

// 이벤트 리스너 설정
function setupEventListeners() {
  console.log("이벤트 리스너 설정");
  
  // 검색 이벤트
  const searchInput = document.getElementById('searchInput');
  const searchBtn = document.getElementById('searchBtn');
  const clearBtn = document.getElementById('clearBtn');
  
  if (searchInput && searchBtn && clearBtn) {
    searchBtn.addEventListener('click', handleSearch);
    searchInput.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') handleSearch();
    });
    clearBtn.addEventListener('click', function() {
      searchInput.value = '';
      handleSearch();
    });
  }
  
  // 필터 이벤트
  const filters = [
    'districtFilter',
    'feeFilter', 
    'typeFilter',
    'timeFilter'
  ];
  
  filters.forEach(filterId => {
    const element = document.getElementById(filterId);
    if (element) {
      element.addEventListener('change', handleSearch);
    }
  });
  
  // 리셋 버튼
  const resetBtn = document.getElementById('resetFiltersBtn');
  if (resetBtn) {
    resetBtn.addEventListener('click', function() {
      // 모든 입력 초기화
      if (searchInput) searchInput.value = '';
      filters.forEach(filterId => {
        const element = document.getElementById(filterId);
        if (element) element.value = '';
      });
      handleSearch();
    });
  }
}

// 검색/필터링 처리
function handleSearch() {
  console.log("검색/필터링 실행");
  
  // 열려있는 정보창 닫기
  if (window.currentInfoWindow) {
    window.currentInfoWindow.close();
    window.currentInfoWindow = null;
  }
  
  const searchTerm = getElementValue('searchInput').toLowerCase();
  const district = getElementValue('districtFilter');
  const fee = getElementValue('feeFilter');
  const type = getElementValue('typeFilter');
  const time = getElementValue('timeFilter');
  
  console.log("필터 조건:", { searchTerm, district, fee, type, time });
  
  // 필터링된 데이터
  const filtered = allParkingData.filter((parking, index) => {
    // 첫 5개 데이터에 대해 상세 로그
    const shouldLog = index < 5;
    
    if (shouldLog) {
      console.log(`[${index}] 필터링 대상:`, {
        name: parking.PKLT_NM,
        addr: parking.ADDR,
        type: parking.PKLT_KND_NM,
        charge: parking.PRK_CRG,
        freeInfo: parking.CHGD_FREE_NM,
        startTime: parking.WD_OPER_BGNG_TM,
        endTime: parking.WD_OPER_END_TM
      });
    }
    
    // 검색어 필터
    if (searchTerm) {
      const name = (parking.PKLT_NM || '').toLowerCase();
      const addr = (parking.ADDR || '').toLowerCase();
      if (!name.includes(searchTerm) && !addr.includes(searchTerm)) {
        if (shouldLog) console.log(`[${index}] 검색어 필터 탈락`);
        return false;
      }
    }
    
    // 구 필터
    if (district) {
      const addr = parking.ADDR || '';
      if (!addr.includes(district)) {
        if (shouldLog) console.log(`[${index}] 구 필터 탈락`);
        return false;
      }
    }
    
    // 요금 필터 개선
    if (fee) {
      // 요금 정보 추출 - 더 다양한 필드 확인
      let chargeAmount = 0;
      const prkCrg = parking.PRK_CRG || '';
      const addCrg = parking.ADD_CRG || '';
      const feePerHour = parking.PRK_CRG_HR || '';
      const feeInfo = (parking.CHGD_FREE_NM || '').toLowerCase();
      const operInfo = (parking.OPER_TM_NM || '').toLowerCase();
      
      // 숫자형 요금 추출 시도
      if (prkCrg && !isNaN(parseInt(prkCrg))) {
        chargeAmount = parseInt(prkCrg);
      } else if (feePerHour && !isNaN(parseInt(feePerHour))) {
        chargeAmount = parseInt(feePerHour);
      } else if (addCrg && !isNaN(parseInt(addCrg))) {
        chargeAmount = parseInt(addCrg);
      }
      
      // 무료 표시 확인
      const isFreeMarked = feeInfo.includes('무료') || 
                          feeInfo.includes('free') ||
                          operInfo.includes('무료') ||
                          prkCrg === '0' ||
                          prkCrg === '' ||
                          prkCrg === null;
      
      if (shouldLog) {
        console.log(`[${index}] 요금 정보:`, {
          fee,
          prkCrg,
          addCrg,
          feePerHour,
          chargeAmount,
          feeInfo,
          isFreeMarked
        });
      }
      
      switch (fee) {
        case 'free':
          // 무료 조건: 요금이 0이거나 무료 표시가 있는 경우
          if (!isFreeMarked && chargeAmount > 0) {
            if (shouldLog) console.log(`[${index}] 무료 필터 탈락 (요금: ${chargeAmount}원)`);
            return false;
          }
          break;
          
        case 'paid':
          // 유료 조건: 요금이 0보다 크고 무료 표시가 없는 경우
          if (isFreeMarked || chargeAmount === 0) {
            if (shouldLog) console.log(`[${index}] 유료 필터 탈락 (무료 또는 0원)`);
            return false;
          }
          break;
          
        case 'low':
          // 저렴: 1원 이상 1000원 이하 (무료 제외)
          if (isFreeMarked || chargeAmount === 0 || chargeAmount > 1000) {
            if (shouldLog) console.log(`[${index}] 저렴 필터 탈락 (${chargeAmount}원)`);
            return false;
          }
          break;
          
        case 'medium':
          // 보통: 1001원 이상 2000원 이하
          if (chargeAmount < 1001 || chargeAmount > 2000) {
            if (shouldLog) console.log(`[${index}] 보통 필터 탈락 (${chargeAmount}원)`);
            return false;
          }
          break;
          
        case 'high':
          // 비싼: 2001원 이상
          if (chargeAmount < 2001) {
            if (shouldLog) console.log(`[${index}] 비싼 필터 탈락 (${chargeAmount}원)`);
            return false;
          }
          break;
      }
    }
    
    // 타입 필터 개선
    if (type) {
      const parkingName = (parking.PKLT_NM || '').toLowerCase();
      const parkingType = (parking.PKLT_KND_NM || '').toLowerCase();
      const operator = (parking.OPRP_NM || '').toLowerCase();
      const allTypeInfo = parkingName + ' ' + parkingType + ' ' + operator;
      
      if (shouldLog) {
        console.log(`[${index}] 타입 정보:`, {
          type,
          parkingName,
          parkingType,
          operator,
          allTypeInfo: allTypeInfo.substring(0, 50)
        });
      }
      
      switch (type) {
        case 'normal':
          // 일반 주차장: 버스가 포함되지 않은 경우
          if (allTypeInfo.includes('버스') || allTypeInfo.includes('bus')) {
            if (shouldLog) console.log(`[${index}] 일반 타입 필터 탈락 (버스 포함)`);
            return false;
          }
          break;
          
        case 'bus':
          // 버스 전용: 버스가 포함된 경우
          if (!allTypeInfo.includes('버스') && !allTypeInfo.includes('bus')) {
            if (shouldLog) console.log(`[${index}] 버스 타입 필터 탈락`);
            return false;
          }
          break;
          
        case 'public':
          // 공영: 공영, 공용, 시립, 구립 등이 포함된 경우
          const isPublic = allTypeInfo.includes('공영') || 
                          allTypeInfo.includes('공용') || 
                          allTypeInfo.includes('시립') || 
                          allTypeInfo.includes('구립') ||
                          allTypeInfo.includes('시청') ||
                          allTypeInfo.includes('구청');
          if (!isPublic) {
            if (shouldLog) console.log(`[${index}] 공영 타입 필터 탈락`);
            return false;
          }
          break;
          
        case 'private':
          // 민영: 공영이 아닌 경우
          const isPrivate = !allTypeInfo.includes('공영') && 
                           !allTypeInfo.includes('공용') && 
                           !allTypeInfo.includes('시립') && 
                           !allTypeInfo.includes('구립') &&
                           !allTypeInfo.includes('시청') &&
                           !allTypeInfo.includes('구청');
          if (!isPrivate) {
            if (shouldLog) console.log(`[${index}] 민영 타입 필터 탈락`);
            return false;
          }
          break;
      }
    }
    
    // 운영시간 필터 개선
    if (time) {
      const startTime = parking.WD_OPER_BGNG_TM || parking.OPER_BGNG_TM || '';
      const endTime = parking.WD_OPER_END_TM || parking.OPER_END_TM || '';
      const operTime = parking.OPER_TM_NM || '';
      
      if (shouldLog) {
        console.log(`[${index}] 운영시간 정보:`, {
          time,
          startTime,
          endTime,
          operTime
        });
      }
      
      switch (time) {
        case '24h':
          // 24시간: 24시간 관련 표시가 있거나 00:00-24:00 형태
          const is24Hour = operTime.includes('24시간') ||
                          operTime.includes('24') ||
                          endTime.includes('24:00') ||
                          endTime.includes('2400') ||
                          (startTime.includes('00:00') && endTime.includes('24:00'));
          if (!is24Hour) {
            if (shouldLog) console.log(`[${index}] 24시간 필터 탈락`);
            return false;
          }
          break;
          
        case 'day':
          // 주간: 06:00-22:00 형태 (일반적인 주간 운영)
          if (startTime && endTime) {
            const startHour = getHourFromTime(startTime);
            const endHour = getHourFromTime(endTime);
            
            if (startHour === null || endHour === null || startHour > 8 || endHour < 20) {
              if (shouldLog) console.log(`[${index}] 주간 필터 탈락 (${startHour}:00-${endHour}:00)`);
              return false;
            }
          }
          break;
          
        case 'extended':
          // 연장: 06:00-24:00 형태 (늦은 시간까지 운영)
          if (startTime && endTime) {
            const startHour = getHourFromTime(startTime);
            const endHour = getHourFromTime(endTime);
            
            if (startHour === null || endHour === null || startHour > 8 || endHour < 23) {
              if (shouldLog) console.log(`[${index}] 연장 필터 탈락 (${startHour}:00-${endHour}:00)`);
              return false;
            }
          }
          break;
          
        case 'night':
          // 야간: 22시 이후까지 운영
          if (endTime) {
            const endHour = getHourFromTime(endTime);
            if (endHour === null || endHour < 22) {
              if (shouldLog) console.log(`[${index}] 야간 필터 탈락 (${endHour}:00까지)`);
              return false;
            }
          }
          break;
      }
    }
    
    if (shouldLog) {
      console.log(`[${index}] 모든 필터 통과!`);
    }
    
    return true;
  });
  
  console.log("필터링 결과:", filtered.length, "개");
  
  // 마커 업데이트
  displayFilteredMarkers(filtered);
  updateResultCount(filtered.length);
}

// 시간 문자열에서 시간(hour) 추출하는 헬퍼 함수
function getHourFromTime(timeStr) {
  if (!timeStr) return null;
  
  // "09:00", "0900", "09시" 등 다양한 형태 처리
  const match = timeStr.match(/(\d{1,2})/);
  if (match) {
    const hour = parseInt(match[1]);
    return (hour >= 0 && hour <= 24) ? hour : null;
  }
  return null;
}

// 필터링된 마커 표시
function displayFilteredMarkers(filteredData) {
  clearMarkers();
  
  filteredData.forEach(parking => {
    const lat = parseFloat(parking.LAT);
    const lng = parseFloat(parking.LOT);
    
    if (!isNaN(lat) && !isNaN(lng)) {
      const marker = createMarker(parking, lat, lng);
      if (marker) markers.push(marker);
    }
  });
  
  console.log("필터링된 마커 표시 완료:", markers.length, "개");
}

// 헬퍼 함수들
function getElementValue(id) {
  const element = document.getElementById(id);
  return element ? element.value.trim() : '';
}

function updateResultCount(count) {
  const element = document.getElementById('resultCount');
  if (element) {
    if (count === allParkingData.length) {
      element.textContent = `전체 주차장 ${count}개를 표시하고 있습니다.`;
    } else {
      element.textContent = `${count}개의 주차장을 찾았습니다. (전체 ${allParkingData.length}개 중)`;
    }
  }
}

function showErrorMessage(message) {
  const element = document.getElementById('resultCount');
  if (element) {
    element.textContent = message;
    element.style.color = 'red';
  }
}
