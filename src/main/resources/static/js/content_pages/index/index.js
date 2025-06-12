import { createApp, onMounted } from "vue";

const app = createApp({
  template: '<div id="kakao-map-container" style="width:100%;height:100%;"></div>',
  setup() {
    onMounted(() => {
      kakao.maps.load(() => {
        const mapContainer = document.getElementById("kakao-map-container");
        const mapOption = {
          center: new kakao.maps.LatLng(37.5666805, 126.9784147),
          level: 6,
        };

        const map = new kakao.maps.Map(mapContainer, mapOption);

        var zoomControl = new kakao.maps.ZoomControl();
        map.addControl(zoomControl, kakao.maps.ControlPosition.BOTTOMRIGHT);

        var mapTypeControl = new kakao.maps.MapTypeControl();
        map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOP);

        map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);
      });

      // 지도 전체보기 토글
      const btn = document.getElementById("map-toggle-btn");
      const mapContainer = document.getElementById("mapContainer");
      if (btn && mapContainer) {
        btn.addEventListener("click", () => {
          const isExpanded = mapContainer.classList.toggle("expanded");
          btn.textContent = isExpanded ? "지도 축소하기" : "지도 전체보기";
        });
      }
    });
  },
});

app.mount("#vmap");
