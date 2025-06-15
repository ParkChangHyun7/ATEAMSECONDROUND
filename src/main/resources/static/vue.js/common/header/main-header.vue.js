import { createApp } from 'vue';

createApp({
  data() {
    return {
      isMenuOpen: false,
      dropdownOverlayHeight: '0px',
    };
  },
  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
      this.updateDropdownOverlay();
    },
    updateDropdownOverlay() {
      const overlay = document.getElementById('dropdown-menu-overlay');
      if (overlay) {
        if (this.isMenuOpen) {
          overlay.classList.add('show');
          // 높이 계산 로직은 여기에 추가 예정
          // 현재 페이지가 index.jsp인지 확인
          const currentPage = window.location.pathname;
          if (currentPage === '/' || currentPage === '/index.jsp') {
            // index.jsp 페이지일 경우 지도의 높이를 가져와서 적용
            const mapContainer = document.getElementById('vmap');
            if (mapContainer) {
              this.dropdownOverlayHeight = `${mapContainer.offsetHeight}px`;
            } else {
              this.dropdownOverlayHeight = 'auto'; // 폴백 값
            }
          } else {
            // 다른 페이지일 경우 메뉴 크기만큼 높이 설정 (임시 값)
            this.dropdownOverlayHeight = '300px'; // 예시 값, 실제 메뉴 높이에 따라 조정 필요
          }
          overlay.style.height = this.dropdownOverlayHeight;
        } else {
          overlay.classList.remove('show');
          overlay.style.height = '0px';
        }
      }
    },
  },
  mounted() {
    // 페이지 로드 시 오버레이 초기 상태 설정 (필요에 따라)
    this.updateDropdownOverlay();
    // 창 크기 변경 시 오버레이 높이 재조정 (반응형)
    window.addEventListener('resize', this.updateDropdownOverlay);

    // 초기 드롭다운 메뉴 닫힘 상태 유지
    this.isMenuOpen = false;
    const overlay = document.getElementById('dropdown-menu-overlay');
    if (overlay) {
        overlay.style.height = '0px';
        overlay.classList.remove('show');
    }
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.updateDropdownOverlay);
  }
}).mount('#main-header-app'); 