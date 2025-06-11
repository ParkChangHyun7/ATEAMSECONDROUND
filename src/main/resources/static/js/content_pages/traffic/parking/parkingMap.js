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

      const formatTime = (timeStr) => {
        if (!timeStr || timeStr.length !== 4) return '-'
        return timeStr.slice(0, 2) + ':' + timeStr.slice(2)
      }

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
          const isBusOnly = (p.PKLT_KND_NM || '').includes('버스') || (p.PKLT_NM || '').includes('버스')
          const isFree = (p.CHGD_FREE_NM || '').includes('무료')

          const defaultImageSrc = isBusOnly ? '/images/bus-parking.png' : '/images/parking-lot.png'
          const hoverImageSrc = isBusOnly ? '/images/bus-parking-hover.png' : '/images/parking-lot-hover.png'

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
            ? `<tr><td style="padding:4px; border:1px solid #ccc;">기본요금</td><td style="padding:4px; border:1px solid #ccc;">${p.ADD_CRG}원 / ${p.PRK_HM}분</td></tr>`
            : ''

          const extraChargeRow = !isFree && p.ADD_CRG && p.ADD_UNIT_TM
            ? `<tr><td style="padding:4px; border:1px solid #ccc;">추가요금</td><td style="padding:4px; border:1px solid #ccc;">${p.ADD_CRG}원 / ${p.ADD_UNIT_TM}분</td></tr>`
            : ''

          const monthlyChargeRow = p.MONTLY_CMMT_CHRG_AMT && p.MONTLY_CMMT_CHRG_AMT !== '0' && p.MONTLY_CMMT_CHRG_AMT !== 0
            ? `<tr><td style="padding:4px; border:1px solid #ccc;">월정기권금액</td><td style="padding:4px; border:1px solid #ccc;">${p.MONTLY_CMMT_CHRG_AMT}원</td></tr>`
            : ''

          const content = `
            <div style="
              padding:10px;
              font-size:13px;
              background:white;
              border-radius:8px;
              box-shadow: 0 2px 6px rgba(0,0,0,0.2);
              min-width: 280px;
              max-width: 350px;
              box-sizing: border-box;
              word-break: break-word;
              white-space: normal;">
              <b style="font-size:14px;">${p.PKLT_NM || '이름없음'}</b>
              <table style="border-collapse: collapse; width: 100%; margin-top: 6px;">
                <tr><td style="padding:4px; border:1px solid #ccc;">주소</td><td style="padding:4px; border:1px solid #ccc;">${p.ADDR || '-'}</td></tr>
                <tr><td style="padding:4px; border:1px solid #ccc;">전화</td><td style="padding:4px; border:1px solid #ccc;">${p.TELNO || '-'}</td></tr>
                <tr><td style="padding:4px; border:1px solid #ccc;">주차장 구분</td><td style="padding:4px; border:1px solid #ccc;">${p.PKLT_KND_NM || '-'}</td></tr>
                <tr><td style="padding:4px; border:1px solid #ccc;">운영 구분</td><td style="padding:4px; border:1px solid #ccc;">${p.OPER_SE_NM || '-'}</td></tr>
                <tr><td style="padding:4px; border:1px solid #ccc;">총 주차면</td><td style="padding:4px; border:1px solid #ccc;">${p.TPKCT || '-'}</td></tr>
                <tr><td style="padding:4px; border:1px solid #ccc;">유료 여부</td><td style="padding:4px; border:1px solid #ccc;">${p.CHGD_FREE_NM || '-'}</td></tr>
                ${basicChargeRow}
                ${extraChargeRow}
                ${monthlyChargeRow}
                <tr><td style="padding:4px; border:1px solid #ccc;">운영시간</td><td style="padding:4px; border:1px solid #ccc;">
                  평일: ${formatTime(p.WD_OPER_BGNG_TM)} ~ ${formatTime(p.WD_OPER_END_TM)}<br>
                  주말: ${formatTime(p.HLDY_BGNG_TM)} ~ ${formatTime(p.HLDY_END_TM)}
                </td></tr>
              </table>
            </div>`

          const infowindow = new kakao.maps.InfoWindow({ content })

          kakao.maps.event.addListener(marker, 'click', () => infowindow.open(map, marker))

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