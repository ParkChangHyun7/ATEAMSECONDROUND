import { createApp, onMounted, ref } from "vue";

/* 1. 카카오 지도 Vue 인스턴스 */
const mapApp = createApp({
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

        map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.BOTTOMRIGHT);
        map.addControl(new kakao.maps.MapTypeControl(), kakao.maps.ControlPosition.TOP);
        map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);
      });

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
mapApp.mount("#vmap");

/* 2. 대기오염 Vue 인스턴스 */
const airApp = createApp({
  setup() {
    const airData = ref([]);

    const fetchAirQuality = async () => {
      try {
        const res = await fetch("/api/air"); // Spring 프록시 컨트롤러 호출
        const json = await res.json();
        airData.value = json.RealtimeCityAir.row;
      } catch (e) {
        console.error("대기오염 데이터 실패:", e);
      }
    };

    const getGradeClass = (grade) => {
      switch (grade) {
        case "좋음":
          return "grade-good";
        case "보통":
          return "grade-normal";
        case "나쁨":
          return "grade-bad";
        case "매우나쁨":
          return "grade-verybad";
        default:
          return "";
      }
    };

    onMounted(() => {
      fetchAirQuality();
    });

    return {
      airData,
      getGradeClass,
    };
  },

  template: `
    <div>
      <h4>서울 대기오염 정보</h4>
      <ul>
        <li
          v-for="item in airData"
          :key="item.MSRSTE_NM"
          :class="getGradeClass(item.IDEX_NM)"
        >
          {{ item.MSRSTE_NM }}: {{ item.PM10 }}㎍/㎥ ({{ item.IDEX_NM }})
        </li>
      </ul>
    </div>
  `,
});
airApp.mount("#air-info-box");
