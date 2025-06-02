const app = Vue.createApp({
  data() {
    return {
      trafficFlowLinks: [],
      trafficData: [],
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
        level: 7,
      });
    },
    loadData() {
      Promise.all([
        fetch('/data/trafficflowmap_links2.geojson').then(res => res.json()),
        fetch('/api/traffic/trafficflowmap').then(res => res.json())
      ]).then(([geoJson, trafficData]) => {
        this.trafficFlowLinks = geoJson.features;
        this.trafficData = trafficData.items.item; // ITS API 구조상 item 배열임!
        this.drawTrafficFlowLines();
      });
    },
    drawTrafficFlowLines() {
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
