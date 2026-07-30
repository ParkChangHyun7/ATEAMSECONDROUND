<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- 지도 영역 -->
<div class="map-wrapper">
  <div class="map_container" id="mapContainer">
    <div class="mapview" id="vmap"></div>
    <button id="map-toggle-btn">지도 전체보기</button>
  </div>
</div>

<main class="main">
  <div class="middle-section">
    <div class="background-section">
      <div class="yellow-bg"></div>
      <div class="navy-bg"></div>
    </div>
    <div class="top-section">
      <div class="yellow-top">
        <div class="weather-info" id="weather-app">
          <div class="weather-temp-sky-rain">
            <span class="weather-temp"></span>
            <div class="weather-sky"></div>
            <span class="weather-rain"></span>
          </div>
          <div class="weather-details">
            <span class="wds-default">미세</span>
            <span class="dust-value-0"></span>
            <span class="wds-default">초미세</span>
            <span class="dust-value-0"></span>
          </div>
        </div>
      </div>
      <div class="navy-top"></div>
    </div>
  </div>

  <!-- 공지사항 -->
  <div class="content-section">
    <div style="margin-bottom: 10px;">
      <h3 style="color:rgb(182, 28, 199); text-align:center; margin-bottom:5px;">공지사항</h3>
      <div class="content-box">
        <div id="notice-swiper-app">
          <div class="swiper" id="notice-swiper" style="width: 100%; max-width: 500px; position: relative;">
            <div class="swiper-wrapper">
              <div class="swiper-slide" v-for="notice in notices" :key="notice.id" :style="getSlideStyle(notice)">
                <p style="color: black; font-weight: bold; margin: 0 0 8px; text-align: center;">
                  {{ notice.title }}
                </p>
                <img v-if="notice.image" :src="notice.image" alt="공지 이미지"
                     style="width: 100%; max-height: 180px; object-fit: cover;" />
                <div v-if="notice.content" v-html="notice.content"
                     style="color: black; font-size: 14px; line-height: 1.6; text-align: left; padding-top: 8px;"></div>
              </div>
            </div>
            <div class="swiper-controls">
              <div class="swiper-button-prev"></div>
              <button id="notice-swiper-toggle" class="swiper-toggle-btn">
                <span class="material-icons">pause</span>
              </button>
              <div class="swiper-button-next"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 실시간 돌발상황 -->
    <div style="margin-bottom: 10px;">
      <h3 style="color:rgb(185, 101, 17); text-align:center; margin-bottom:5px;">실시간 돌발상황</h3>
      <div class="content-box">
        <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
        <div id="traffic-events-app" style="padding: 10px;">
          <!-- 알림 -->
          <div v-if="showNotification" class="notification-alert">
            {{ notificationMessage }}
          </div>

          <div v-if="loading" class="loading-text">
            🔄 실시간 돌발상황 로딩 중...
          </div>
          <div v-else-if="events.length === 0" class="no-events">
            ✅ 현재 돌발상황이 없습니다
          </div>
          <div v-else class="events-container" @scroll="handleScroll">
            <div class="realtime-status">
              🟢 실시간 업데이트 중 ({{ lastUpdate }})
            </div>
            
            <!-- 카테고리 필터 -->
            <div class="category-filters">
              <button 
                v-for="category in categories" 
                :key="category.key"
                @click="selectedCategory = category.key"
                :class="['filter-btn', { active: selectedCategory === category.key }]"
              >
                {{ category.icon }} {{ category.label }}
              </button>
            </div>

            <div class="events-list">
              <div v-for="event in displayEvents" :key="event.id" class="event-item" :class="getEventClass(event.eventType)">
                <div class="event-header">
                  <span class="event-type">{{ getEventCategory(event.eventType) }}</span>
                  <span class="event-time">{{ formatTime(event.startDate) }}</span>
                </div>
                <div class="event-content">
                  <div class="event-road">📍 {{ event.roadName || '도로명 정보 없음' }}</div>
                  <div class="event-message">{{ event.message || '상세 정보 없음' }}</div>
                </div>
                <div class="event-actions">
                  <button @click="openModal(event)" class="action-btn detail-btn">상세보기</button>
                  <button @click="goToEventMap(event)" class="action-btn map-btn">지도에서보기</button>
                </div>
              </div>
              
              <!-- 로딩 더보기 -->
              <div v-if="isLoadingMore" class="loading-more">
                <div class="loading-spinner"></div>
                <span>더 많은 돌발상황을 불러오는 중...</span>
              </div>
              
              <!-- 더보기 버튼 (스크롤이 끝에 도달하지 않았을 때) -->
              <div v-if="hasMoreEvents && !isLoadingMore" class="load-more-btn-container">
                <button @click="loadMoreEvents" class="load-more-btn">
                  더보기 ({{ filteredEvents.length - displayEvents.length }}건 더 있음)
                </button>
              </div>
            </div>
          </div>

          <!-- 상세 정보 모달 -->
          <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
              <div class="modal-header">
                <h3>{{ getEventCategory(selectedEvent?.eventType) }} 상세 정보</h3>
                <button @click="closeModal" class="close-btn">×</button>
              </div>
              <div class="modal-body">
                <div class="detail-item">
                  <strong>📍 위치:</strong> {{ selectedEvent?.roadName || '정보 없음' }}
                </div>
                <div class="detail-item">
                  <strong>🕐 발생시간:</strong> {{ formatTime(selectedEvent?.startDate) }}
                </div>
                <div class="detail-item">
                  <strong>📝 상세내용:</strong> {{ selectedEvent?.message || '상세 정보 없음' }}
                </div>
                <div class="detail-item" v-if="selectedEvent?.coordX && selectedEvent?.coordY">
                  <strong>🗺️ 좌표:</strong> {{ selectedEvent.coordY }}, {{ selectedEvent.coordX }}
                </div>
                <div class="detail-item" v-if="selectedEvent?.eventType">
                  <strong>🏷️ 유형:</strong> {{ selectedEvent.eventType }}
                </div>
              </div>
              <div class="modal-footer">
                <button @click="goToEventMap(selectedEvent)" class="modal-action-btn">지도에서 보기</button>
                <button @click="closeModal" class="modal-action-btn secondary">닫기</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 전체 돌발상황 보기 버튼을 박스 바로 밑에 위치 -->
      <div class="more-events-outside">
        <a href="/traffic/eventMap" class="more-link">
          전체 돌발상황 지도에서 보기 →
        </a>
      </div>
    </div>

    <!-- 서울 대기오염 정보 -->
    <div style="margin-bottom: 10px;">
      <h3 style="color:rgb(51, 176, 13); text-align:center; margin-bottom:5px;">서울 대기오염 정보</h3>
      <div class="content-box">
        <div class="index-box"><span>●</span><span>●</span><span>●</span></div>
        <div class="air-box">
          <video class="air-bg-video" autoplay muted loop playsinline>
            <source src="/videos/AirQuality/sky.mp4" type="video/mp4" />
          </video>
          <div id="air-info-box"></div>
        </div>
      </div>
    </div>
  </div>

  <div class="ITS-link">
    <div class="ITS-link-container">
      <h3 class="its-title">관련 교통정보 사이트</h3>
      <div class="its-links-grid">
        <a href="https://www.its.go.kr" target="_blank" class="its-link-item">
          <div class="its-icon">🚗</div>
          <div class="its-info">
            <h4>국가교통정보센터</h4>
            <p>전국 교통정보 통합 서비스</p>
          </div>
        </a>
        <a href="https://topis.seoul.go.kr" target="_blank" class="its-link-item">
          <div class="its-icon">🏙️</div>
          <div class="its-info">
            <h4>서울교통정보</h4>
            <p>서울시 실시간 교통정보</p>
          </div>
        </a>
        <a href="https://www.ex.co.kr" target="_blank" class="its-link-item">
          <div class="its-icon">🛣️</div>
          <div class="its-info">
            <h4>한국도로공사</h4>
            <p>고속도로 교통정보</p>
          </div>
        </a>
        <a href="https://www.molit.go.kr" target="_blank" class="its-link-item">
          <div class="its-icon">🏛️</div>
          <div class="its-info">
            <h4>국토교통부</h4>
            <p>교통정책 및 공지사항</p>
          </div>
        </a>
      </div>
    </div>
  </div>
</main>

<script type="module">
  import { createApp, ref, onMounted } from 'vue'

  const App = {
    setup() {
      const notices = ref([
        {
          id: 1,
          type: '공지',
          title: '[공지] 6월 정기 시스템 점검 안내',
          content: `안녕하세요, 서울교통정보센터입니다.<br>보다 안정적인 서비스 제공을 위해 다음과 같이 시스템 점검을 진행합니다.<br><br>🛠 점검 일시: 2025년 6월 17일(화) 09:00 ~ 18:00<br>🔌 점검 내용: 서버 정기 보안 업데이트 및 트래픽 최적화<br>🚫 서비스 영향: 점검 시간 동안 지도/대중교통정보 일부 서비스 접속 불가`
        },
        {
          id: 2,
          type: '안내',
          title: '[안내] 로그인 보안 강화 적용 예정',
          content: `보다 안전한 교통정보 이용을 위해 로그인 시 보안 인증 절차가 강화될 예정입니다.<br><br>🔐 적용 일자: 2025년 6월 25일(화)<br>📌 주요 변경: 비밀번호 변경 주기 도입, 2단계 인증 시범 적용`
        },
        {
          id: 3,
          type: '이벤트',
          title: '[이벤트] 대중교통 이용 캠페인',
          image: '/images/transport-campaign.png'
        }
      ])

      const getSlideStyle = (notice) => {
        const base = {
          padding: '15px',
          borderRadius: '10px',
          height: 'auto',
          boxSizing: 'border-box'
        }
        if (notice.type === '공지') return { ...base, backgroundColor: '#fffbe6' }
        if (notice.type === '안내') return { ...base, backgroundColor: '#e3f2fd' }
        return base
      }

      onMounted(() => {
        const swiper = new Swiper('#notice-swiper', {
          direction: 'horizontal',
          loop: true,
          autoplay: {
            delay: 7000,
            disableOnInteraction: false,
            pauseOnMouseEnter: true
          },
          navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev'
          }
        })

        const toggleBtn = document.getElementById('notice-swiper-toggle')
        const icon = toggleBtn.querySelector('.material-icons')
        let isPlaying = true

        toggleBtn.addEventListener('click', () => {
          if (isPlaying) {
            swiper.autoplay.stop()
            icon.textContent = 'play_arrow'
          } else {
            swiper.autoplay.start()
            icon.textContent = 'pause'
          }
          isPlaying = !isPlaying
        })
      })

      return { notices, getSlideStyle }
    }
  }

  createApp(App).mount('#notice-swiper-app')
</script>

<script type="module">
  import { createApp, ref, onMounted } from 'vue'

  const AirInfoApp = {
    setup() {
      const airInfo = ref('로딩 중...')

      onMounted(async () => {
        try {
          const res = await fetch('/api/indexWeather')
          if (!res.ok) throw new Error('불러오기 실패')
          const d = await res.json()

          airInfo.value = `
            <span class="hover-line">• 중구: ${d.junggu}㎍/㎥ (${d.jungguStatus})</span>
            <span class="hover-line">• 종로구: ${d.jongrogu}㎍/㎥ (${d.jongroguStatus})</span>
            <span class="hover-line">• 용산구: ${d.yongsangu}㎍/㎥ (${d.yongsanguStatus})</span>
            <span class="hover-line">• 은평구: ${d.eunpyeong}㎍/㎥ (${d.eunpyeongStatus})</span>
            <span class="hover-line">• 서대문구: ${d.seodaemun}㎍/㎥ (${d.seodaemunStatus})</span>
            <span class="hover-line">• 마포구: ${d.mapo}㎍/㎥ (${d.mapoStatus})</span>
          `
        } catch (e) {
          airInfo.value = '<span class="hover-line">정보를 불러오는 데 실패했습니다.</span>'
        }
      })

      return { airInfo }
    },
    template: `<div class="air-info-text" v-html="airInfo"></div>`
  }

  createApp(AirInfoApp).mount('#air-info-box')
</script>

<script type="module">
  import { createApp, ref, computed, watch, onMounted, onUnmounted } from 'vue'

  const TrafficEventsApp = {
    setup() {
      const events = ref([])
      const loading = ref(true)
      const lastUpdate = ref('')
      const showNotification = ref(false)
      const notificationMessage = ref('')
      const selectedCategory = ref('all')
      const showModal = ref(false)
      const selectedEvent = ref(null)
      const displayCount = ref(3)
      const isLoadingMore = ref(false)
      let updateInterval = null
      let previousEventCount = 0

      const filteredEvents = computed(() => {
        return selectedCategory.value === 'all' 
          ? events.value 
          : events.value.filter(event => getEventCategory(event.eventType).includes(getCategoryIcon(selectedCategory.value)))
      })

      const displayEvents = computed(() => {
        return filteredEvents.value.slice(0, displayCount.value)
      })

      const hasMoreEvents = computed(() => {
        return displayCount.value < filteredEvents.value.length
      })

      const categories = [
        { key: 'all', label: '전체', icon: '📋' },
        { key: 'accident', label: '교통사고', icon: '🚗' },
        { key: 'construction', label: '공사', icon: '🚧' },
        { key: 'weather', label: '기상', icon: '🌧️' },
        { key: 'disaster', label: '재난', icon: '⚠️' },
        { key: 'other', label: '기타', icon: '📢' }
      ]

      const getCategoryIcon = (category) => {
        const categoryMap = {
          'accident': '🚗',
          'construction': '🚧', 
          'weather': '🌧️',
          'disaster': '⚠️',
          'other': '📢'
        }
        return categoryMap[category] || '📢'
      }

      const getEventCategory = (type) => {
        if (!type) return '기타'
        const cleanType = type.replace(/<[^>]+>/g, '').toLowerCase()
        if (cleanType.includes('공사')) return '🚧 공사'
        if (cleanType.includes('사고') || cleanType.includes('추돌') || cleanType.includes('정체')) return '🚗 교통사고'
        if (cleanType.includes('기상') || cleanType.includes('눈') || cleanType.includes('비') || cleanType.includes('안개')) return '🌧️ 기상'
        if (cleanType.includes('재난') || cleanType.includes('침수') || cleanType.includes('지반') || cleanType.includes('붕괴')) return '⚠️ 재난'
        if (cleanType.includes('기타돌발')) return '🚨 기타돌발'
        return '📢 기타'
      }

      const getEventClass = (type) => {
        if (!type) return 'event-other'
        const cleanType = type.replace(/<[^>]+>/g, '').toLowerCase()
        if (cleanType.includes('공사')) return 'event-construction'
        if (cleanType.includes('사고') || cleanType.includes('추돌') || cleanType.includes('정체')) return 'event-accident'
        if (cleanType.includes('기상')) return 'event-weather'
        if (cleanType.includes('재난')) return 'event-disaster'
        return 'event-other'
      }

      const formatTime = (dateStr) => {
        if (!dateStr) return ''
        try {
          const date = new Date(dateStr)
          return date.toLocaleTimeString('ko-KR', { 
            hour: '2-digit', 
            minute: '2-digit' 
          })
        } catch {
          return ''
        }
      }

      const showNotificationAlert = (message) => {
        notificationMessage.value = message
        showNotification.value = true
        setTimeout(() => {
          showNotification.value = false
        }, 5000)
      }

      const openModal = (event) => {
        selectedEvent.value = event
        showModal.value = true
      }

      const closeModal = () => {
        showModal.value = false
        selectedEvent.value = null
      }

      const goToEventMap = (event) => {
        // 지도 페이지로 이동하면서 해당 이벤트 정보 전달
        const params = new URLSearchParams({
          lat: event.coordY,
          lng: event.coordX,
          eventId: event.id || Date.now()
        })
        window.open(`/traffic/eventMap?${params.toString()}`, '_blank')
      }

      const loadMoreEvents = () => {
        if (hasMoreEvents.value && !isLoadingMore.value) {
          isLoadingMore.value = true
          setTimeout(() => {
            displayCount.value += 3
            isLoadingMore.value = false
          }, 300) // 로딩 효과를 위한 딜레이
        }
      }

      const handleScroll = (event) => {
        const container = event.target
        const scrollTop = container.scrollTop
        const scrollHeight = container.scrollHeight
        const clientHeight = container.clientHeight
        
        // 스크롤이 하단 근처에 도달했을 때 더 로드
        if (scrollTop + clientHeight >= scrollHeight - 50) {
          loadMoreEvents()
        }
      }

      const resetDisplayCount = () => {
        displayCount.value = 3
      }

      const loadTrafficEvents = async () => {
        try {
          const response = await fetch('/api/traffic/events')
          if (!response.ok) throw new Error('API 호출 실패')
          
          const data = await response.json()
          const newEvents = data?.body?.items || []
          
          // 최신 순으로 정렬
          events.value = newEvents.sort((a, b) => {
            const dateA = new Date(a.startDate || 0)
            const dateB = new Date(b.startDate || 0)
            return dateB - dateA
          })

          // 새로운 돌발상황 알림
          if (previousEventCount > 0 && newEvents.length > previousEventCount) {
            const newCount = newEvents.length - previousEventCount
            showNotificationAlert(`🚨 새로운 돌발상황 ${newCount}건이 발생했습니다!`)
          }
          previousEventCount = newEvents.length
          
          lastUpdate.value = new Date().toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
          })
          
          loading.value = false
          console.log('메인페이지 돌발상황 업데이트:', lastUpdate.value, `(${events.value.length}건)`)
        } catch (error) {
          console.error('돌발상황 로딩 실패:', error)
          loading.value = false
        }
      }

      onMounted(() => {
        // 초기 로드
        loadTrafficEvents()
        
        // 1분마다 실시간 업데이트
        updateInterval = setInterval(loadTrafficEvents, 60000)
      })

      onUnmounted(() => {
        if (updateInterval) {
          clearInterval(updateInterval)
        }
      })

      // 카테고리 변경 시 표시 개수 리셋
      watch(selectedCategory, () => {
        resetDisplayCount()
      })

      return {
        events,
        loading,
        lastUpdate,
        displayEvents,
        filteredEvents,
        hasMoreEvents,
        isLoadingMore,
        categories,
        selectedCategory,
        showNotification,
        notificationMessage,
        showModal,
        selectedEvent,
        getEventCategory,
        getEventClass,
        formatTime,
        openModal,
        closeModal,
        goToEventMap,
        handleScroll,
        loadMoreEvents
      }
    }
  }

  createApp(TrafficEventsApp).mount('#traffic-events-app')
</script>