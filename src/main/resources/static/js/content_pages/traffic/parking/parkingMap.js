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
      let currentInfoWindow = null

      // 지도 클릭 시 InfoWindow 닫기
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
          console.log('주차장 데이터:', p); // 데이터 확인용 로그
          const lat = parseFloat(String(p.LAT || '').trim())
          const lng = parseFloat(String(p.LOT || '').trim())
          if (isNaN(lat) || isNaN(lng)) continue

          const latlng = new kakao.maps.LatLng(lat, lng)

          const isBusOnly = (p.PKLT_KND_NM || '').includes('버스') || (p.PKLT_NM || '').includes('버스')
          const isFree = (p.CHGD_FREE_NM || '').includes('무료')

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
            ? `<tr><td style="border:1px solid #ccc; padding:4px;">기본요금</td><td style="border:1px solid #ccc; padding:4px;">${p.ADD_CRG}원 / ${p.PRK_HM}분</td></tr>` : ''

          const extraChargeRow = !isFree
            ? `<tr><td style=\"border:1px solid #ccc; padding:4px;\">추가요금</td><td style=\"border:1px solid #ccc; padding:4px;\">${p.ADD_RATES && p.ADD_TIME_RATES ? p.ADD_RATES + '원 / ' + p.ADD_TIME_RATES + '분' : '-'}</td></tr>` : ''

          const monthlyChargeRow = p.MONTLY_CMMT_CHRG_AMT && p.MONTLY_CMMT_CHRG_AMT !== '0'
            ? `<tr><td style=\"border:1px solid #ccc; padding:4px;\">월정기권금액</td><td style=\"border:1px solid #ccc; padding:4px;\">${p.MONTLY_CMMT_CHRG_AMT}원</td></tr>` : ''

          const content = `
            <div style="padding:10px; font-size:13px; background:white; border-radius:8px;
                        box-shadow:0 2px 6px rgba(0,0,0,0.2); min-width:280px; max-width:350px;">
              <b style="font-size:14px;">${p.PKLT_NM || '이름없음'}</b>
              <table style="width:100%; margin-top:6px; border-collapse:collapse;">
                <tr><td style="border:1px solid #ccc; padding:4px;">주소</td><td style="border:1px solid #ccc; padding:4px;">${p.ADDR || '-'}</td></tr>
                <tr><td style="border:1px solid #ccc; padding:4px;">전화</td><td style="border:1px solid #ccc; padding:4px;">${p.TELNO || '-'}</td></tr>
                <tr><td style="border:1px solid #ccc; padding:4px;">주차장 구분</td><td style="border:1px solid #ccc; padding:4px;">${p.PKLT_KND_NM || '-'}</td></tr>
                <tr><td style="border:1px solid #ccc; padding:4px;">운영 구분</td><td style="border:1px solid #ccc; padding:4px;">${p.OPER_SE_NM || '-'}</td></tr>
                <tr><td style="border:1px solid #ccc; padding:4px;">총 주차면</td><td style="border:1px solid #ccc; padding:4px;">${p.TPKCT || '-'}</td></tr>
                <tr><td style="border:1px solid #ccc; padding:4px;">유료 여부</td><td style="border:1px solid #ccc; padding:4px;">${p.CHGD_FREE_NM || '-'}</td></tr>
                ${basicChargeRow}
                ${extraChargeRow}
                ${monthlyChargeRow}
                <tr><td style="border:1px solid #ccc; padding:4px;">운영시간</td><td style="border:1px solid #ccc; padding:4px;">
                  평일: ${formatTime(p.WEEKDAY_BEGIN_TIME)} - ${formatTime(p.WEEKDAY_END_TIME)}<br>
                  주말: ${formatTime(p.WEEKEND_BEGIN_TIME) !== '-' || formatTime(p.WEEKEND_END_TIME) !== '-' ? formatTime(p.WEEKEND_BEGIN_TIME) + ' - ' + formatTime(p.WEEKEND_END_TIME) : '-'}
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
