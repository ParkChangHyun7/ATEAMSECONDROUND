// src/main/resources/static/js/traffic/parking/parkingMap.js

document.addEventListener("DOMContentLoaded", () => {
    kakao.maps.load(async () => {
        const mapContainer = document.getElementById("map");
        if (!mapContainer) {
            console.error("#map 요소를 찾을 수 없습니다.");
            return;
        }

        const map = new kakao.maps.Map(mapContainer, {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 5,
        });

        try {
            const response = await fetch("/api/parking");
            const responseJson = await response.json();
            const parkingList = responseJson?.GetParkingInfo?.row ?? [];

            const bounds = new kakao.maps.LatLngBounds();

            parkingList.forEach((parking, idx) => {
                const lat = parseFloat(parking.PKLT_LAT);
                const lng = parseFloat(parking.PKLT_LNG);

                if (isNaN(lat) || isNaN(lng)) {
                    console.warn(`위치값 오류 → 제외됨: `, parking);
                    return;
                }

                const marker = new kakao.maps.Marker({
                    position: new kakao.maps.LatLng(lat, lng),
                    map: map,
                });

                const infowindow = new kakao.maps.InfoWindow({
                    content: `
                        <div style="padding:5px;font-size:14px;">
                            <b>${parking.PKLT_NM}</b><br/>
                            ${parking.ADDR}<br/>
                            ${parking.TELNO}<br/>
                            per hr: ${parking.PRK_HM}min / additional: ${parking.ADD_CRG}원
                        </div>
                    `,
                });

                kakao.maps.event.addListener(marker, 'click', () => {
                    infowindow.open(map, marker);
                });

                bounds.extend(new kakao.maps.LatLng(lat, lng)); // 지도 범위 확장
            });

            map.setBounds(bounds); // 마커들 전체가 보이도록 지도 조정

        } catch (error) {
            console.error("주차장 데이터를 불러오는 데 실패했습니다.", error);
        }
    });
});