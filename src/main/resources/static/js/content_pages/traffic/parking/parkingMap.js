import { createApp, onMounted } from 'vue'

createApp({
  template: '<div></div>', // 실제 Vue 템플릿은 JSP에 있기 때문에 비워둠

  setup() {
    onMounted(async () => {
      const container = document.getElementById('map')
      if (!container) return

      const map = new kakao.maps.Map(container, {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 5
      })

      const bounds = new kakao.maps.LatLngBounds()
      let currentInfoWindow = null // 하나만 열리게 하기 위한 전역 변수

      // 지도 클릭 시 열린 창 닫기
      kakao.maps.event.addListener(map, 'click', () => {
        if (currentInfoWindow) {
          currentInfoWindow.close()
          currentInfoWindow = null
        }
      })

      const formatTime = (timeStr) => {
        if (!timeStr || timeStr.length !== 4) return '-'
        return timeStr.slice(0, 2) + ':' + timeStr.slice(2)
      }

      try {
        const response = await fetch('/api/parking')
        const text = await response.text()

        let parsed
        try {
          parsed = JSON.parse(text)
        } catch (e) {
          console.error('JSON 파싱 실패:', e)
          return
        }

        const parkingList = parsed?.GetParkInfo?.row || []
        console.log('총 주차장 수:', parkingList.length)

        let validCount = 0

        for (const p of parkingList) {
          const lat = parseFloat(String(p.LAT || '').trim())
          const lng = parseFloat(String(p.LOT || '').trim())
          if (isNaN(lat) || isNaN(lng)) continue

          const latlng = new kakao.maps.LatLng(lat, lng)

          const isBusOnly = (p.PKLT_KND_NM || '').includes('버스') || (p.PKLT_NM || '').includes('버스')
          const isFree = (p.CHGD_FREE_NM || '').includes('무료')

          // 캐시 방지용 쿼리 추가
          const defaultImageSrc = isBusOnly
            ? '/images/parking/bus-parking.png?v=1'
            : '/images/parking/parking-lot.png?v=1'

          const hoverImageSrc = isBusOnly
            ? '/images/parking/bus-parking-hover.png?v=1'
            : '/images/parking/parking-lot-hover.png?v=1'

          const defaultImageSize = new kakao.maps.Size(32, 32)
          const hoverImageSize = new kakao.maps.Size(64, 64)

          const markerImage = new kakao.maps.MarkerImage(defaultImageSrc, defaultImageSize, {
            offset: new kakao.maps.Point(16, 32)
          })

          const marker = new kakao.maps.Marker({
            position: latlng,
            map,
            image: markerImage
          })

          const basicChargeRow = !isFree && p.ADD_CRG && p.PRK_HM
            ? `<tr><td>기본요금</td><td>${p.ADD_CRG}원 / ${p.PRK_HM}분</td></tr>` : ''

          const extraChargeRow = !isFree && p.ADD_CRG && p.ADD_UNIT_TM
            ? `<tr><td>추가요금</td><td>${p.ADD_CRG}원 / ${p.ADD_UNIT_TM}분</td></tr>` : ''

          const monthlyChargeRow = p.MONTLY_CMMT_CHRG_AMT && p.MONTLY_CMMT_CHRG_AMT !== '0'
            ? `<tr><td>월정기권금액</td><td>${p.MONTLY_CMMT_CHRG_AMT}원</td></tr>` : ''

          const content = `
            <div style="padding:10px; font-size:13px; background:white; border-radius:8px;
                        box-shadow:0 2px 6px rgba(0,0,0,0.2); min-width:280px; max-width:350px;">
              <b style="font-size:14px;">${p.PKLT_NM || '이름없음'}</b>
              <table style="width:100%; margin-top:6px; border-collapse:collapse;">
                <tr><td>주소</td><td>${p.ADDR || '-'}</td></tr>
                <tr><td>전화</td><td>${p.TELNO || '-'}</td></tr>
                <tr><td>주차장 구분</td><td>${p.PKLT_KND_NM || '-'}</td></tr>
                <tr><td>운영 구분</td><td>${p.OPER_SE_NM || '-'}</td></tr>
                <tr><td>총 주차면</td><td>${p.TPKCT || '-'}</td></tr>
                <tr><td>유료 여부</td><td>${p.CHGD_FREE_NM || '-'}</td></tr>
                ${basicChargeRow}
                ${extraChargeRow}
                ${monthlyChargeRow}
                <tr><td>운영시간</td><td>
                  평일: ${formatTime(p.WD_OPER_BGNG_TM)} ~ ${formatTime(p.WD_OPER_END_TM)}<br>
                  주말: ${formatTime(p.HLDY_BGNG_TM)} ~ ${formatTime(p.HLDY_END_TM)}
                </td></tr>
              </table>
            </div>`

          const infowindow = new kakao.maps.InfoWindow({ content })

          kakao.maps.event.addListener(marker, 'click', () => {
            if (currentInfoWindow) currentInfoWindow.close()
            infowindow.open(map, marker)
            currentInfoWindow = infowindow
          })

          kakao.maps.event.addListener(marker, 'mouseover', () => {
            const hoverImage = new kakao.maps.MarkerImage(hoverImageSrc, hoverImageSize, {
              offset: new kakao.maps.Point(24, 48)
            })
            marker.setImage(hoverImage)
          })

          kakao.maps.event.addListener(marker, 'mouseout', () => {
            const originalImage = new kakao.maps.MarkerImage(defaultImageSrc, defaultImageSize, {
              offset: new kakao.maps.Point(16, 32)
            })
            marker.setImage(originalImage)
          })

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
