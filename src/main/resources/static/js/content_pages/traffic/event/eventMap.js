document.addEventListener("DOMContentLoaded", () => {
  // URL 파라미터 확인
  const urlParams = new URLSearchParams(window.location.search);
  const targetLat = urlParams.get('lat');
  const targetLng = urlParams.get('lng');
  const eventId = urlParams.get('eventId');
  
  // 초기 지도 중심점 설정 (파라미터가 있으면 해당 위치, 없으면 기본 위치)
  const initialCenter = (targetLat && targetLng) 
    ? new kakao.maps.LatLng(parseFloat(targetLat), parseFloat(targetLng))
    : new kakao.maps.LatLng(37.5665, 126.978);
  
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: initialCenter,
    level: targetLat && targetLng ? 5 : 7  // 특정 위치로 온 경우 더 확대
  });

  const eventListEl = document.getElementById("eventList");
  const eventListWrapper = document.getElementById("eventListWrapper");
  const filterButtons = document.querySelectorAll(".filter-btn");
  const searchInput = document.getElementById("searchInput");
  const scrollTopBtn = document.getElementById("scrollToTopBtn");

  let allEvents = [];
  let allMarkers = [];
  let openInfoWindow = null;
  let activeFilterType = "all";
  let markerToListItemMap = new Map();

  function mapCategoryName(type) {
    if (!type) return "기타";
    const cleanType = type.replace(/<[^>]+>/g, "").toLowerCase();
    if (cleanType.includes("공사")) return "공사";
    if (cleanType.includes("사고") || cleanType.includes("추돌") || cleanType.includes("정체")) return "교통사고";
    if (cleanType.includes("기상") || cleanType.includes("눈") || cleanType.includes("비") || cleanType.includes("안개")) return "기상";
    if (cleanType.includes("재난") || cleanType.includes("침수") || cleanType.includes("지반") || cleanType.includes("붕괴")) return "재난";
    if (cleanType.includes("기타돌발")) return "기타돌발";
    return "기타";
  }

  // 날짜 포맷 변환 함수 (YYYYMMDDHHMMSS -> YYYY/MM/DD)
  function formatDateTime(dateTimeStr) {
    if (!dateTimeStr || dateTimeStr.length < 8) return dateTimeStr;
    
    const str = String(dateTimeStr);
    const year = str.substring(0, 4);
    const month = str.substring(4, 6);
    const day = str.substring(6, 8);
    
    // 날짜만 반환 (시간 부분 제외)
    return `${year}/${month}/${day}`;
  }

  function renderEvents(filterType = "all") {
	
	if (openInfoWindow) {
	  openInfoWindow.close();
	  openInfoWindow = null;
	}

	
    eventListEl.innerHTML = "";
    allMarkers.forEach(marker => marker.setMap(null));
    allMarkers = [];
    markerToListItemMap.clear();
    activeFilterType = filterType;

    const filtered = filterType === "all"
      ? allEvents
      : allEvents.filter(ev => mapCategoryName(ev.eventType) === filterType);

    if (filtered.length === 0) {
      const tr = document.createElement("tr");
      tr.innerHTML = `<td colspan="3" style="text-align: center; color: gray; padding: 20px;"><em>해당 카테고리에 데이터가 없습니다.</em></td>`;
      eventListEl.appendChild(tr);
      return;
    }

    filtered.forEach(event => createListAndMarker(event));
    
    // URL 파라미터로 특정 이벤트가 지정된 경우 해당 이벤트 강조
    if (eventId && targetLat && targetLng) {
      setTimeout(() => {
        highlightTargetEvent(targetLat, targetLng);
      }, 500);
    }
  }

  function highlightTargetEvent(lat, lng) {
    // 해당 위치의 마커 찾기
    const targetMarker = allMarkers.find(marker => {
      const pos = marker.getPosition();
      return Math.abs(pos.getLat() - parseFloat(lat)) < 0.001 && 
             Math.abs(pos.getLng() - parseFloat(lng)) < 0.001;
    });
    
    if (targetMarker) {
      // 마커 클릭 이벤트 트리거
      kakao.maps.event.trigger(targetMarker, 'click');
      
      // 알림 표시
      const notification = document.createElement('div');
      notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: linear-gradient(45deg, #4CAF50, #45a049);
        color: white;
        padding: 12px 20px;
        border-radius: 25px;
        font-size: 14px;
        font-weight: bold;
        box-shadow: 0 4px 20px rgba(76, 175, 80, 0.4);
        z-index: 10000;
        animation: slideInRight 0.5s ease-out;
      `;
      notification.textContent = '🎯 메인페이지에서 선택한 돌발상황입니다!';
      document.body.appendChild(notification);
      
      setTimeout(() => {
        notification.remove();
      }, 4000);
    }
  }

  function createListAndMarker(event) {
    const lat = parseFloat(String(event.coordY).replace(",", "."));
    const lon = parseFloat(String(event.coordX).replace(",", "."));
    if (isNaN(lat) || isNaN(lon)) return;

    const pos = new kakao.maps.LatLng(lat, lon);
    
    // 이벤트 타입에 따라 마커 설정
    const eventCategory = mapCategoryName(event.eventType);
    let marker;
    let imageSrc = null;
    
    // 카테고리별 마커 이미지 설정
    switch (eventCategory) {
      case "공사":
        imageSrc = '/images/traffic/construction.png';
        break;
      case "재난":
        imageSrc = '/images/crisis.png';
        break;
      case "기상":
        imageSrc = '/images/lightning.png';
        break;
      case "기타돌발":
        imageSrc = '/images/traffic-jam.png';
        break;
      default:
        // 교통사고나 기타는 기본 마커 사용
        marker = new kakao.maps.Marker({ position: pos, map });
        break;
    }
    
    // 커스텀 마커가 필요한 경우
    if (imageSrc) {
      const imageSize = new kakao.maps.Size(32, 32);
      const imageOption = { offset: new kakao.maps.Point(16, 32) };
      
      const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
      marker = new kakao.maps.Marker({ 
        position: pos, 
        map,
        image: markerImage
      });
      
      // 마우스 오버 효과 추가
      const hoverImageSize = new kakao.maps.Size(40, 40);
      const hoverImageOption = { offset: new kakao.maps.Point(20, 40) };
      const hoverMarkerImage = new kakao.maps.MarkerImage(imageSrc, hoverImageSize, hoverImageOption);
      
      kakao.maps.event.addListener(marker, 'mouseover', () => {
        marker.setImage(hoverMarkerImage);
      });
      
      kakao.maps.event.addListener(marker, 'mouseout', () => {
        marker.setImage(markerImage);
      });
    }
    
    allMarkers.push(marker);

    const infoContent = `
      <div style="padding:10px; font-size:13px; background:white; border-radius:8px;
                  box-shadow:0 2px 6px rgba(0,0,0,0.2); min-width:300px; max-width:400px;">
        <b style="font-size:14px; color:#d32f2f;">🚨 ${mapCategoryName(event.eventType)}</b>
        <table style="width:100%; margin-top:8px; border-collapse:collapse; font-size:12px;">
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold; width:30%;">구분</td>
            <td style="border:1px solid #ddd; padding:6px;">${mapCategoryName(event.eventType)}</td>
          </tr>
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold;">도로명</td>
            <td style="border:1px solid #ddd; padding:6px;">${event.roadName || "정보 없음"}</td>
          </tr>
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold;">상황내용</td>
            <td style="border:1px solid #ddd; padding:6px; line-height:1.4;">${event.message || "정보 없음"}</td>
          </tr>
          ${event.startDate ? `
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold;">시작일시</td>
            <td style="border:1px solid #ddd; padding:6px;">${formatDateTime(event.startDate)}</td>
          </tr>` : ''}
          ${event.endDate ? `
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold;">종료예정</td>
            <td style="border:1px solid #ddd; padding:6px;">${formatDateTime(event.endDate)}</td>
          </tr>` : ''}
          ${event.eventId ? `
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold;">이벤트ID</td>
            <td style="border:1px solid #ddd; padding:6px; font-family:monospace; font-size:10px;">${event.eventId}</td>
          </tr>` : ''}
          <tr>
            <td style="border:1px solid #ddd; padding:6px; background:#f8f9fa; font-weight:bold;">위치</td>
            <td style="border:1px solid #ddd; padding:6px; font-size:10px; color:#666;">
              위도: ${event.coordY}<br>경도: ${event.coordX}
            </td>
          </tr>
        </table>
        <div style="margin-top:8px; text-align:center;">
          <small style="color:#666; font-size:10px;">📍 클릭하여 해당 위치로 이동</small>
        </div>
      </div>
    `;
    const info = new kakao.maps.InfoWindow({ content: infoContent });

    const tr = document.createElement("tr");
    tr.className = "event-row";
    tr.innerHTML = `
      <td class="event-category"><strong>${mapCategoryName(event.eventType)}</strong></td>
      <td class="event-road">${event.roadName || "정보 없음"}</td>
      <td class="event-message">${event.message || "메시지 없음"}</td>
    `;

    tr.addEventListener("click", () => {
      document.querySelectorAll("#eventList tr").forEach(el => el.classList.remove("active-list-item"));
      tr.classList.add("active-list-item");

      // ✅ 부드러운 중앙 이동 (딜레이 추가)
      setTimeout(() => {
        map.panTo(pos);
      }, 10);

      kakao.maps.event.trigger(marker, "click");
    });

    markerToListItemMap.set(marker, tr);
    eventListEl.appendChild(tr);

    kakao.maps.event.addListener(marker, "click", () => {
      if (openInfoWindow) openInfoWindow.close();

      // ✅ 마커 클릭 시 지도 중심으로 부드럽게 이동
      setTimeout(() => {
        map.panTo(pos);
      }, 10);

      info.open(map, marker);
      openInfoWindow = info;

      document.querySelectorAll("#eventList tr").forEach(el => el.classList.remove("active-list-item"));
      const targetTr = markerToListItemMap.get(marker);
      if (targetTr) {
        targetTr.classList.add("active-list-item");
        targetTr.scrollIntoView({ behavior: "smooth", block: "center" });
      }
    });
  }

  searchInput.addEventListener("input", () => {
    const keyword = searchInput.value.trim().toLowerCase();
    const filtered = allEvents.filter(ev => {
      const msg = (ev.message || "").toLowerCase();
      const road = (ev.roadName || "").toLowerCase();
      return msg.includes(keyword) || road.includes(keyword);
    });

    eventListEl.innerHTML = "";
    allMarkers.forEach(marker => marker.setMap(null));
    allMarkers = [];

    if (filtered.length === 0) {
      const tr = document.createElement("tr");
      tr.innerHTML = `<td colspan="3" style="text-align: center; color: gray; padding: 20px;"><em>검색 결과가 없습니다.</em></td>`;
      eventListEl.appendChild(tr);
      return;
    }

    filtered.forEach(event => createListAndMarker(event));
  });

  fetch("/api/traffic/events")
    .then(res => res.json())
    .then(data => {
      allEvents = data?.body?.items || [];
      renderEvents("all");
      document.querySelector('.filter-btn[data-type="all"]')?.classList.add("active");
    });

  filterButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      filterButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      renderEvents(btn.dataset.type);
    });
  });

  kakao.maps.event.addListener(map, 'click', () => {
    if (openInfoWindow) {
      openInfoWindow.close();
      openInfoWindow = null;
      document.querySelectorAll("#eventList tr").forEach(el => el.classList.remove("active-list-item"));
    }
  });

  // ✅ Top 버튼 제어
  eventListWrapper.addEventListener("scroll", () => {
    scrollTopBtn.style.display = eventListWrapper.scrollTop > 150 ? "block" : "none";
  });

  scrollTopBtn.addEventListener("click", () => {
    eventListWrapper.scrollTo({ top: 0, behavior: "smooth" });
  });
});
