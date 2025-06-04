const app = Vue.createApp({
  data() {
    return {
      trafficFlowLinks: [],
      trafficData: [],
      map: null
    };
  },
  mounted() {
    this.initMap();
    this.loadData();
  },
  methods: {
    initMap() {
      this.map = new kakao.maps.Map(document.getElementById("map"), {
        center: new kakao.maps.LatLng(37.55, 126.98),
        level: 7
      });
    },
    loadData() {
      Promise.all([
        fetch('/api/trafficflowmap_geojson').then(res => res.json()),
        fetch('/api/traffic/trafficflowmap').then(res => res.json())
      ])
      .then(([geoJson, trafficData]) => {
        console.log('GeoJSON:', geoJson);
        console.log('TrafficData:', trafficData);

        // GeoJSON → Features 적용
        this.trafficFlowLinks = geoJson.features;

        // TrafficData 구조 유연하게 처리
        if (trafficData.body && trafficData.body.items) {
          this.trafficData = trafficData.body.items.item;
        } else if (trafficData.items) {
          this.trafficData = trafficData.items.item;
        } else {
          console.error('TrafficData 구조 오류:', trafficData);
          this.trafficData = [];
        }

        this.drawTrafficFlowLines();
      })
      .catch(error => {
        console.error('데이터 로드 오류:', error);
      });
    },
    drawTrafficFlowLines() {
      console.log('drawTrafficFlowLines 호출');

      this.trafficData.forEach(item => {
        const feature = this.trafficFlowLinks.find(f =>
          f.properties.LINK_ID === item.linkId
        );

        if (feature) {
          const path = feature.geometry.coordinates.map(coord =>
            new kakao.maps.LatLng(coord[1], coord[0])
          );

          const color = this.getCongestionColor(item.speed);

          new kakao.maps.Polyline({
            map: this.map,
            path: path,
            strokeWeight: 5,
            strokeColor: color,
            strokeOpacity: 0.8,
            strokeStyle: 'solid'
          });
        }
      });
    },
    getCongestionColor(speed) {
      if (speed >= 80) return '#00FF00'; // 원활 (초록)
      if (speed >= 40) return '#FFFF00'; // 서행 (노랑)
      return '#FF0000'; // 정체 (빨강)
    }
  }
});

app.mount("#traffic-flow-map-app");
