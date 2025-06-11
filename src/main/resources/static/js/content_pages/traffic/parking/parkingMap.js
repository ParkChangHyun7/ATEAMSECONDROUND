import { createApp, onMounted } from 'vue'

createApp({
  template: '<div></div>', // 실제 Vue 템플릿은 JSP에 있기 때문에 비워둠

  setup() {
    onMounted(async () => {
      // 지도를 표시할 <div id="map"> 요소 가져오기
      const container = document.getElementById('map')
      if (!container) return

      // 카카오 맵 초기화: 중심 좌표와 줌 레벨 설정
      const map = new kakao.maps.Map(container, {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 중심
        level: 5
      })

      // 지도의 마커 영역 자동 조정용 경계 객체
      const bounds = new kakao.maps.LatLngBounds()

      // 현재 열려 있는 InfoWindow 추적용 변수 (하나만 열리게 하기 위함)
      let currentInfoWindow = null

      // 운영시간 표기를 사람이 읽을 수 있게 HHMM → HH:MM으로 변환하는 함수
      const formatTime = (timeStr) => {
        if (!timeStr || timeStr.length !== 4) return '-'
        return timeStr.slice(0, 2) + ':' + timeStr.slice(2)
      }

      try {
        // 서버에서 실시간 주차장 정보 가져오기 (서울시 Open API)
        const response = await fetch('/api/parking')
        const text = await response.text()
        const parsed = JSON.parse(text)
        const parkingList = parsed?.GetParkInfo?.row || []

        console.log('총 주차장 수:', parkingList.length)

        let validCount = 0 // 유효 좌표를 가진 마커 개수

        for (const p of parkingList) {
          // 위도, 경도 파싱
          const rawLat = String(p.LAT || '').trim()
          const rawLng = String(p.LOT || '').trim()
          const lat = parseFloat(rawLat)
          const lng = parseFloat(rawLng)
          if (isNaN(lat) || isNaN(lng)) continue // 숫자가 아니면 마커 건너뜀

          const latlng = new kakao.maps.LatLng(lat, lng)

          // 🚍 버스 전용 주차장인지 여부 확인
          const isBusOnly = (p.PKLT_KND_NM || '').includes('버스') || (p.PKLT_NM || '').includes('버스')

          // 💸 무료 주차장 여부 확인
          const isFree = (p.CHGD_FREE_NM || '').includes('무료')

          // 마커 이미지 설정 (일반/버스용)
          const defaultImageSrc = isBusOnly ? '/images/bus-parking.png' : '/images/parking-lot.png'
          const hoverImageSrc = isBusOnly ? '/images/bus-parking-hover.png' : '/images/parking-lot-hover.png'

          const defaultImageSize = new kakao.maps.Size(32, 32)
          const hoverImageSize = new kakao.maps.Size(64, 64)

          // 기본 마커 이미지 생성
          const markerImage = new kakao.maps.MarkerImage(defaultImageSrc, defaultImageSize, {
            offset: new kakao.maps.Point(16, 32)
          })

          // 마커 생성 및 지도에 표시
          const marker = new kakao.maps.Marker({
            position: latlng,
            map,
            image: markerImage
          })

          // 💰 요금 표 형식으로 내용 생성
          const basicChargeRow = !isFree && p.ADD_CRG && p.PRK_HM
            ? `<tr><td>기본요금</td><td>${p.ADD_CRG}원 / ${p.PRK_HM}분</td></tr>` : ''

          const extraChargeRow = !isFree && p.ADD_CRG && p.ADD_UNIT_TM
            ? `<tr><td>추가요금</td><td>${p.ADD_CRG}원 / ${p.ADD_UNIT_TM}분</td></tr>` : ''

          const monthlyChargeRow = p.MONTLY_CMMT_CHRG_AMT && p.MONTLY_CMMT_CHRG_AMT !== '0'
            ? `<tr><td>월정기권금액</td><td>${p.MONTLY_CMMT_CHRG_AMT}원</td></tr>` : ''

          // 🧾 인포윈도우에 들어갈 HTML 내용
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

          // InfoWindow 생성
          const infowindow = new kakao.maps.InfoWindow({ content })

          // 📍 마커 클릭 시 InfoWindow 열기 (이전 창 닫기)
          kakao.maps.event.addListener(marker, 'click', () => {
            if (currentInfoWindow) currentInfoWindow.close() //  기존 창 닫기
            infowindow.open(map, marker)                     // 새 창 열기
            currentInfoWindow = infowindow                   // 현재 창으로 설정
          })

          // 마우스 오버 시 마커 확대 및 hover 이미지 적용
          kakao.maps.event.addListener(marker, 'mouseover', () => {
            const hoverImage = new kakao.maps.MarkerImage(hoverImageSrc, hoverImageSize, {
              offset: new kakao.maps.Point(24, 48)
            })
            marker.setImage(hoverImage)
          })

          // 마우스 아웃 시 원래 이미지로 복귀
          kakao.maps.event.addListener(marker, 'mouseout', () => {
            const originalImage = new kakao.maps.MarkerImage(defaultImageSrc, defaultImageSize, {
              offset: new kakao.maps.Point(16, 32)
            })
            marker.setImage(originalImage)
          })

          bounds.extend(latlng) // 지도 범위에 포함
          validCount++
        }

        // 마커가 있으면 지도 범위 자동 조정
        if (validCount > 0) map.setBounds(bounds)
        else console.warn('유효한 마커가 없습니다.')
      } catch (e) {
        console.error('마커 로딩 실패:', e)
      }
    })
  }
}).mount('#map')
