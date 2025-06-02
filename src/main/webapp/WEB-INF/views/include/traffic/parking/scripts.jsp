<script type="module">
import { createApp, onMounted } from 'vue'

createApp({
  template: '<div></div>',
  setup() {
    onMounted(async () => {
      const container = document.getElementById('map')

      const options = {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울시청
        level: 5,
        disableWheel: true
      }

      const map = new kakao.maps.Map(container, options)
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
          const rawLng = String(p.LNG || '').trim()

          // 디버깅 출력
          console.log('📌 ADDR:', p.ADDR, '위도:', rawLat, '경도:', rawLng)

          // 유효한 좌표값 필터링
          if (!rawLat || !rawLng || rawLat === '0' || rawLng === '0') continue

          const lat = parseFloat(rawLat)
          const lng = parseFloat(rawLng)

          if (isNaN(lat) || isNaN(lng)) continue

          const latlng = new kakao.maps.LatLng(lat, lng)

          const marker = new kakao.maps.Marker({
            map,
            position: latlng
          })

          const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;">${p.PKPL_NM || '이름없음'}</div>`
          })

          kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map, marker))
          kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close())

          bounds.extend(latlng)
          validCount++
        }

        if (validCount > 0) {
          map.setBounds(bounds)
        } else {
          console.warn('✅ 유효 마커 없음: map.setBounds() 생략됨')
        }

      } catch (e) {
        console.error('❌ 마커 표시 중 오류 발생:', e)
      }
    })
  }
}).mount('#map')
</script>
