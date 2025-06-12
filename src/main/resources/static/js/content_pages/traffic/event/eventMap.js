document.addEventListener("DOMContentLoaded", () => {
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.5665, 126.978),
    level: 7
  });

  const eventListEl = document.getElementById("eventList");
  const filterButtons = document.querySelectorAll(".filter-btn");

  let allEvents = [];
  let allMarkers = [];
  let openInfoWindow = null;

  // ✅ 한글 기반 카테고리 분류 함수
  function mapCategoryName(type) {
    if (!type) return "기타";

    if (type.includes("공사")) return "공사";
    if (type.includes("사고") || type.includes("추돌") || type.includes("정체")) return "교통사고";
    if (type.includes("기상") || type.includes("안개") || type.includes("눈") || type.includes("비")) return "기상";
    if (type.includes("기타돌발")) return "기타돌발";
    if (type.includes("재난") || type.includes("침수") || type.includes("지반") || type.includes("붕괴")) return "재난";
    return "기타";
  }

  function renderEvents(filterType = "all") {
    eventListEl.innerHTML = "";
    allMarkers.forEach(marker => marker.setMap(null));
    allMarkers = [];

    const filtered = filterType === "all"
      ? allEvents
      : allEvents.filter(ev => mapCategoryName(ev.eventType) === filterType);

    if (filtered.length === 0) {
      const li = document.createElement("li");
      li.innerHTML = `<em style="color: gray;">해당 카테고리에 데이터가 없습니다.</em>`;
      eventListEl.appendChild(li);
      return;
    }

    filtered.forEach(event => {
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

      kakao.maps.event.addListener(marker, 'click', () => {
        if (openInfoWindow) openInfoWindow.close();
        info.open(map, marker);
        openInfoWindow = info;
      });

      const li = document.createElement("li");
      li.innerHTML = `
        <strong>${mapCategoryName(event.eventType)}</strong><br>
        <span style="font-size: 12px;">${event.message || "정보 없음"}</span>
      `;
      li.addEventListener("click", () => {
        map.panTo(pos);
        kakao.maps.event.trigger(marker, 'click');
      });

      eventListEl.appendChild(li);
    });
  }

  let isMounted = true; // 페이지가 살아있는지 추적

  // 페이지가 unload되면 플래그 변경
  window.addEventListener("beforeunload", () => {
    isMounted = false;
  });

  // API 호출
  fetch("/api/traffic/events")
    .then(res => res.json())
    .then(data => {
      if (!isMounted) return; // 페이지 떠났으면 아무것도 안함

      allEvents = data?.body?.items || [];
      renderEvents("all");

      const allBtn = document.querySelector('.filter-btn[data-type="all"]');
      if (allBtn) allBtn.classList.add("active");
    })
    .catch(err => {
      console.error("API 오류:", err);

      if (!isMounted) return; // 페이지 떠났으면 무시
      alert("돌발 상황 데이터를 불러오지 못했습니다.");
    });


  // ✅ 버튼 클릭 시 필터링
  filterButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      filterButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      const type = btn.dataset.type;
      renderEvents(type);
    });
  });

  // ✅ 지도 클릭 시 인포윈도우 닫기
  kakao.maps.event.addListener(map, 'click', () => {
    if (openInfoWindow) {
      openInfoWindow.close();
      openInfoWindow = null;
    }
  });
});
