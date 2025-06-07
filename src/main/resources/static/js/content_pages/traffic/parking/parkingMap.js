import { createApp, onMounted } from 'vue'

createApp({
  template: '<div></div>',
  setup() {
    onMounted(async () => {
      const container = document.getElementById('map')
      if (!container) return

      const map = new kakao.maps.Map(container, {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 5
      })

      const bounds = new kakao.maps.LatLngBounds()

      try {
        const response = await fetch('/api/parking')
        const text = await response.text()
        const parsed = JSON.parse(text)
        const parkingList = parsed?.GetParkInfo?.row || []

        console.log('총 주차장 수:', parkingList.length)

        let validCount = 0

        for (const p of parkingList) {
          const rawLat = String(p.LAT || '').trim()
          const rawLng = String(p.LOT || '').trim()

          const lat = parseFloat(rawLat)
          const lng = parseFloat(rawLng)

          if (isNaN(lat) || isNaN(lng)) continue

          const latlng = new kakao.maps.LatLng(lat, lng)

          const marker = new kakao.maps.Marker({ position: latlng, map })
          
          const content = `
            <div style="padding:5px; font-size:13px;">
              <b>${p.PKLT_NM || '이름없음'}</b><br>
              ${p.ADDR || '주소없음'}<br>
              ${p.TELNO ? p.TELNO + '<br>' : ''}
              기본요금: ${p.PRK_HM || '-'}분 / 추가요금: ${p.ADD_CRG || '-'}원
            </div>`

          const infowindow = new kakao.maps.InfoWindow({ content })

          kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map, marker))
          kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close())

          bounds.extend(latlng)
          validCount++
        }

        if (validCount > 0) map.setBounds(bounds)
        else console.warn('유효한 마커가 없습니다.')

      } catch (e) {
        console.error('마커 로딩 실패:', e)
      }
    })
  }
}).mount('#map')