import { createApp } from 'vue';

createApp({
  data() {
    // data is a function in Vue 3
    return {
      isLoggedIn: false,
      nickname: null,
      role: null, // 운영자 메뉴 표시를 위해 role 추가
    };
  },
  mounted() {
    this.checkUserStatus();
  },
  methods: {
    async checkUserStatus() {
      console.log("사용자 인증 상태 확인 중...");
      try {
        // CSRF 헤더 가져오기
        const csrfHeaders = window.MyApp?.utils?.getCsrfHeadersAsObject();

        const headers = { ...csrfHeaders }; // GET 요청에 필요한 추가 헤더가 있다면 여기에 추가

        const response = await fetch("/usercheck", {
          method: "GET",
          headers: headers,
        });

        let responseData = null;
        if (response.ok) {
          try {
            responseData = await response.json();
          } catch (e) {
            console.warn("/usercheck 로부터의 JSON 응답 파싱 실패");
          }
        }

        console.log("/usercheck 응답:", responseData);

        if (response.ok && responseData?.success) {
          this.isLoggedIn = true;
          this.nickname = responseData.nickname || null;
          this.role = responseData.role || null; // role 값 저장
          console.log("사용자 로그인 상태, 닉네임, 역할 확인 완료: 닉네임=", this.nickname, ", 레벨=", this.role);
        } else {
          console.log("사용자 로그인 상태 확인: 로그아웃 상태 또는 오류 발생");
          this.isLoggedIn = false;
          this.nickname = null;
          this.role = null; // 로그아웃 상태이면 role 초기화
        }
      } catch (error) {
        console.error("/usercheck API 호출 중 오류 발생:", error);
        this.isLoggedIn = false;
        this.nickname = null;
        this.role = null; // 오류 발생 시 role 초기화
      }
    },
  },
  template: `
    <template v-if="isLoggedIn">
      <a href="/user/modify" class="auth-link"><span>{{ nickname ? nickname + '님' : '회원' }} 환영합니다!</span></a>
      <span>|</span>
      <a href="/user/logout" class="auth-link"><span>로그아웃</span></a>
      <!-- 운영자 메뉴: 로그인 상태이고 role이 100 이상일 때만 표시 -->
      <template v-if="isLoggedIn && role >= 100">
        <span>|</span>
        <a href="/manage" class="auth-link"><span>운영자</span></a>
      </template>
    </template>
    <template v-else>
      <a href="/user/join" class="auth-link"><span>회원가입</span></a>
      <span>|</span>
      <a href="/user/login" class="auth-link"><span>로그인</span></a>
    </template>
  `,
}).mount("#auth-links-app");
