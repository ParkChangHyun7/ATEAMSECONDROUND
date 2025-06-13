// eventMap.js

// ✅ DOMContentLoaded 보장

document.addEventListener("DOMContentLoaded", () => {
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.5665, 126.978),
    level: 7
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

  function renderEvents(filterType = "all") {
    eventListEl.innerHTML = "";
    allMarkers.forEach(marker => marker.setMap(null));
    allMarkers = [];
    markerToListItemMap.clear();
    activeFilterType = filterType;

    const filtered = filterType === "all"
      ? allEvents
      : allEvents.filter(ev => mapCategoryName(ev.eventType) === filterType);

    if (filtered.length === 0) {
      const li = document.createElement("li");
      li.innerHTML = `<em style="color: gray;">해당 카테고리에 데이터가 없습니다.</em>`;
      eventListEl.appendChild(li);
      return;
    }

    filtered.forEach(event => createListAndMarker(event));
  }

  function createListAndMarker(event) {
    const lat = parseFloat(String(event.coordY).replace(",", "."));
    const lon = parseFloat(String(event.coordX).replace(",", "."));
    if (isNaN(lat) || isNaN(lon)) return;

    const pos = new kakao.maps.LatLng(lat, lon);
    const marker = new kakao.maps.Marker({ position: pos, map });
    allMarkers.push(marker);

    const infoContent = `
      <div class="info-window">
        <strong>${mapCategoryName(event.eventType)}</strong><br>
        도로명: ${event.roadName || "정보 없음"}<br>
        메시지: ${event.message || "정보 없음"}
      </div>
    `;
    const info = new kakao.maps.InfoWindow({ content: infoContent });

    const li = document.createElement("li");
    li.innerHTML = `
      <strong>${mapCategoryName(event.eventType)}</strong><br>
      <span style="font-size: 12px;">도로명: ${event.roadName || "정보 없음"}</span><br>
      <span style="font-size: 12px;">${event.message || "메시지 없음"}</span>
    `;

    li.addEventListener("click", () => {
      document.querySelectorAll("#eventList li").forEach(el => el.classList.remove("active-list-item"));
      li.classList.add("active-list-item");
      map.setCenter(pos); // ✅ 화면 중앙으로 이동
      kakao.maps.event.trigger(marker, "click");
    });

    markerToListItemMap.set(marker, li);
    eventListEl.appendChild(li);

    kakao.maps.event.addListener(marker, "click", () => {
      if (openInfoWindow) openInfoWindow.close();
	  
	  // ✅ 마커 클릭 시 지도 중심 이동
	  map.setCenter(pos); // 또는 부드러운 이동 원할 경우 map.panTo(pos)
	  
      info.open(map, marker);
      openInfoWindow = info;
	  
      document.querySelectorAll("#eventList li").forEach(el => el.classList.remove("active-list-item"));
      const targetLi = markerToListItemMap.get(marker);
      if (targetLi) {
        targetLi.classList.add("active-list-item");
        targetLi.scrollIntoView({ behavior: "smooth", block: "center" });
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
      const li = document.createElement("li");
      li.innerHTML = `<em style="color: gray;">검색 결과가 없습니다.</em>`;
      eventListEl.appendChild(li);
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
      document.querySelectorAll("#eventList li").forEach(el => el.classList.remove("active-list-item"));
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
