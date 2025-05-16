const joinApp = Vue.createApp({
    data() {
        return {
            phoneNumber: '',
            phoneError: null,
            isPhoneValid: false, // 형식 및 중복 검증 통과 여부
            isCheckingPhone: false, // 중복 확인 중 상태
            showAuthCodeInputs: false,
            csrfToken: null,
            csrfHeader: null
        };
    },
    methods: {
        getCsrfHeaders() {
            const tokenElement = document.querySelector('meta[name="_csrf"]');
            const headerElement = document.querySelector('meta[name="_csrf_header"]');

            if (tokenElement && headerElement) {
                this.csrfToken = tokenElement.getAttribute("content");
                this.csrfHeader = headerElement.getAttribute("content");
                // console.log('CSRF Token:', this.csrfToken);
                // console.log('CSRF Header:', this.csrfHeader);
            } else {
                console.error("CSRF 메타 태그를 찾을 수 없거나 비어있습니다.");
            }
        },
        async validateAndCheckPhone() {
            console.log('validateAndCheckPhone 메서드 호출됨! 시점:', new Date().toLocaleTimeString()); // 로그 추가
            // 초기화
            this.phoneError = null;
            this.showAuthCodeInputs = false;
            this.isPhoneValid = false;
            this.isCheckingPhone = true; // 로딩 시작

            const phoneNumberTrimmed = this.phoneNumber.trim();
            const regex1 = /^010-\d{4}-\d{4}$/;
            const regex2 = /^010\d{8}$/;
            let formattedPhoneNumber = '';

            // 형식 검증
            if (regex1.test(phoneNumberTrimmed)) {
                formattedPhoneNumber = phoneNumberTrimmed.replace(/-/g, "");
            } else if (regex2.test(phoneNumberTrimmed)) {
                formattedPhoneNumber = phoneNumberTrimmed;
            } else {
                this.phoneError = "휴대폰 번호 형식에 적합하지 않습니다.";
                this.isCheckingPhone = false; // 로딩 종료
                return;
            }

            // 서버 중복 검증
            try {
                const headers = { 
                    'Content-Type': 'application/json' 
                };
                // CSRF 토큰 헤더 추가
                if (this.csrfHeader && this.csrfToken) {
                    headers[this.csrfHeader] = this.csrfToken;
                } else {
                    console.error("CSRF 헤더 정보를 찾을 수 없어 요청 헤더에 포함하지 못했습니다.");
                    // 필요 시 사용자에게 알림 또는 기본 동작 수행
                    // 여기서는 일단 진행하지만, 실제 운영 시에는 CSRF 토큰 누락은 보안 문제가 될 수 있음
                }

                // '/check-phone' 엔드포인트는 실제 프로젝트의 엔드포인트로 변경해야 합니다.
                const response = await fetch('/users/check-phone', { 
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify({ phone_number: formattedPhoneNumber })
                });

                if (!response.ok) {
                    // 서버 에러 처리 (4xx, 5xx)
                    const errorData = await response.json().catch(() => ({ message: '응답 처리 중 오류가 발생했습니다.' })); // JSON 파싱 실패 대비
                    this.phoneError = errorData.message || "이미 사용 중인 휴대폰 번호입니다.";
                } else {
                    // 성공 (2xx)
                    this.isPhoneValid = true; // 유효한 번호로 확인됨
                    // 성공 메시지 표시 (선택 사항)
                    // this.phoneError = "사용 가능한 휴대폰 번호입니다."; 
                }
            } catch (error) {
                console.error("Error checking phone number:", error);
                this.phoneError = "휴대폰 번호 검증 중 오류가 발생했습니다.";
            } finally {
                this.isCheckingPhone = false; // 로딩 종료
            }
        },
        sendVerificationCode() {
            // 이 메서드는 버튼 클릭 시 호출되지만, 실제 인증번호 전송 로직은 아직 구현되지 않음
            // validateAndCheckPhone 성공 후 isPhoneValid가 true가 되어 버튼이 활성화됨
            console.log("인증번호 전송 요청 로직 실행 (아직 미구현)");
            // 서버에 인증번호 전송 요청 후 성공하면
            // this.showAuthCodeInputs = true;
            
            // 현재 요구사항은 validateAndCheckPhone 성공 시 바로 입력칸 표시이므로,
            // 해당 로직을 validateAndCheckPhone 성공 블록으로 옮길 수 있음
            // 또는 사용자가 '인증번호 전송' 버튼을 눌렀을 때 입력칸을 표시하게 할 수 있음.
            // 여기서는 버튼 클릭 시 입력칸 표시
            if (this.isPhoneValid) { 
                this.showAuthCodeInputs = true;
                // 여기에 실제 서버로 인증번호 전송 요청 API 호출 코드 추가
                console.log(`${this.phoneNumber}로 인증번호 전송 API 호출`);
                this.phoneError = `인증번호를 ${this.phoneNumber}로 전송했습니다. (실제 전송 아님)`; // 임시 메시지
            }
        }
    },
    mounted() {
        console.log('Vue 인스턴스 mounted! 시점:', new Date().toLocaleTimeString()); // 로그 추가
        // Vue 인스턴스가 마운트된 후 CSRF 토큰 가져오기 시도
        this.getCsrfHeaders();
    }
});

// '#join-app' ID를 가진 요소에 Vue 앱 마운트
joinApp.mount('#join-app'); 