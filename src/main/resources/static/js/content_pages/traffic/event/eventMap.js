document.addEventListener("DOMContentLoaded", () => {
  const mapContainer = document.getElementById("map");
  if (!mapContainer) {
    showError("지도를 표시할 div(id='map')가 존재하지 않습니다.");
    throw new Error("지도 컨테이너가 없습니다.");
  }

  const mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.978),
    level: 7,
  };
  const map = new kakao.maps.Map(mapContainer, mapOption);

  function showError(message) {
    let el = document.getElementById("error");
    if (!el) {
      el = document.createElement("div");
      el.id = "error";
      el.style.color = "red";
      el.style.padding = "10px";
      el.style.fontWeight = "bold";
      document.body.appendChild(el);
    }
    el.textContent = message;
  }

  function defaultText(value, fallback = "정보 없음") {
    try {
      if (value === undefined || value === null) return fallback;
      const str = String(value).trim();
      return str === "" ? fallback : str;
    } catch (e) {
      return fallback;
    }
  }

  fetch("/api/traffic/events")
    .then((res) => res.json())
    .then((data) => {
      const events = data?.body?.items || [];

      events.forEach((event) => {
        const lat = parseFloat(String(event.coordY || "0").replace(",", "."));
        const lon = parseFloat(String(event.coordX || "0").replace(",", "."));
        if (isNaN(lat) || isNaN(lon)) return;

        const eventType = defaultText(event.eventType);
        const detailType = defaultText(event.eventDetailType);
        const roadName = defaultText(event.roadName);
        const roadNo = defaultText(event.roadNo);
        const message = defaultText(event.message);

        const marker = new kakao.maps.Marker({
          map: map,
          position: new kakao.maps.LatLng(lat, lon),
          title: eventType,
        });

        const infoContent = `
          <div class="info-window">
            <strong>사건 유형: ${eventType}</strong><br>
            세부 유형: ${detailType}<br>
            도로명: ${roadName}<br>
            차선 유형: ${roadNo}<br>
            메시지: ${message}
          </div>
        `;

        const info = new kakao.maps.InfoWindow({
          content: infoContent,
          removable: true,
        });

        kakao.maps.event.addListener(marker, "click", () => {
          info.open(map, marker);
        });
      });
    })
    .catch((err) => {
      console.error("API 오류:", err);
      showError("데이터를 불러오지 못했습니다. 다시 시도해주세요.");
    });
});

// ✅ 장소 검색 기능 추가
const placesService = new kakao.maps.services.Places();
const searchInput = document.getElementById("searchKeyword");
const searchButton = document.getElementById("searchButton");
let marker = new kakao.maps.Marker({ map: map });

if (searchButton && searchInput) {
  searchButton.addEventListener("click", () => {
    const keyword = searchInput.value.trim();
    if (!keyword) {
      alert("검색어를 입력하세요.");
      return;
    }

    placesService.keywordSearch(keyword, (data, status) => {
      if (status === kakao.maps.services.Status.OK) {
        const place = data[0];
        const latlng = new kakao.maps.LatLng(place.y, place.x);
        map.setCenter(latlng);
        marker.setPosition(latlng);
      } else {
        alert("검색 결과가 없습니다.");
      }
    });
  });
}


