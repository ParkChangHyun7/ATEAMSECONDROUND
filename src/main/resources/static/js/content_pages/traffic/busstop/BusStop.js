fetch("/api/busstops")
  .then(res => res.json())
  .then(data => {
    data.forEach(stop => {
      const marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(stop.latitude, stop.longitude),
        title: stop.name
      });

      // 간단한 정보창
      const infoWindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;font-size:13px;">${stop.name}</div>`
      });

      kakao.maps.event.addListener(marker, "click", () => {
        infoWindow.open(map, marker);
      });
    });
  });
