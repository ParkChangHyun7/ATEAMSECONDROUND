import { createApp, ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue';

const modifyApp = createApp({
  setup() {
    const isLoading = ref(false);
    const isSubmitting = ref(false);
    const isPasswordChecked = ref(false);
    const passwordInput = ref('');
    const passwordError = ref('');
    const serverErrors = reactive({});
    const showPhonePopup = ref(false);
    const phonePopupFields = reactive({
      phone: '',
      formattedValue: '',
      info: '',
      valid: null,
      checking: false,
      infoTimeoutId: null,
    });
    const phoneAuth = reactive({
      isSendingCode: false,
      isTimerActive: false,
      buttonTimerText: '',
      timerInterval: null,
      remainingTime: 180,
      showAuthCodeInputs: false,
      authCode: ['', '', '', '', ''],
      isVerifyingCode: false,
      isPhoneVerified: false,
      isVerificationSent: false,
      authCodeInputs: [],
    });
    const agreements = reactive({
      alba: false,
      marketing: false,
      benefits: false,
    });
    const fields = reactive({
      loginId: '',
      name: '',
      nickname: '',
      email: '',
      birth: '',
      gender: 'N',
      address_postcode: '',
      address_base: '',
      address_detail: '',
      phone: '',
      new_password: '',
      agreement_age: false,
      agreement_service: false,
      agreement_privacy: false,
      agreement_alba: false,
      agreement_marketing: false,
      agreement_benefits: false,
    });
    const isDaumPostcodeOpen = ref(false);
    const canChangeNickname = ref(true);
    const daysLeft = ref(0);

    // 약관 동의 전체 체크(선택만)
    const agreeAll = computed({
      get() {
        return agreements.alba && agreements.marketing && agreements.benefits;
      },
      set(val) {
        agreements.alba = agreements.marketing = agreements.benefits = val;
      }
    });

    // 닉네임 90일 제한 TODO: 실제 구현 필요

    // 핸드폰 번호 변경 팝업 열기
    const openPhonePopup = () => {
      phonePopupFields.phone = '';
      phonePopupFields.formattedValue = '';
      phonePopupFields.info = '';
      phonePopupFields.valid = null;
      phonePopupFields.checking = false;
      phonePopupFields.infoTimeoutId = null;
      phoneAuth.isPhoneVerified = false;
      phoneAuth.showAuthCodeInputs = false;
      phoneAuth.authCode = ['', '', '', '', ''];
      showPhonePopup.value = true;
    };
    const closePhonePopup = () => {
      showPhonePopup.value = false;
    };

    // 핸드폰 번호 유효성 검사 및 인증 로직 (join.js 참고)
    const validatePhone = async () => {
      const value = phonePopupFields.phone;
      if (!value) return;
      phonePopupFields.checking = true;
      phonePopupFields.info = '확인 중...';
      const result = await window.apiService.postRequest('/user/validation/dupl-check', {
        type: 'phone_number',
        value: value,
      });
      phonePopupFields.info = result.message;
      phonePopupFields.valid = result.success;
      phonePopupFields.checking = false;
    };
    const handlePhoneInput = () => {
      phonePopupFields.valid = null;
      phonePopupFields.info = '';
      if (phonePopupFields.infoTimeoutId) clearTimeout(phonePopupFields.infoTimeoutId);
      phonePopupFields.infoTimeoutId = setTimeout(validatePhone, 300);
    };
    const sendVerificationCode = async () => {
      if (!phonePopupFields.valid) return;
      phoneAuth.isSendingCode = true;
      const result = await window.apiService.postRequest('/user/verification/phone-verify-send', {
        type: 'phone',
        value: phonePopupFields.phone,
      });
      if (result.success) {
        phoneAuth.isVerificationSent = true;
        phoneAuth.showAuthCodeInputs = true;
        startTimer();
      } else {
        phonePopupFields.info = result.message || '인증번호 전송 실패';
      }
      phoneAuth.isSendingCode = false;
    };
    const startTimer = () => {
      if (phoneAuth.timerInterval) clearInterval(phoneAuth.timerInterval);
      phoneAuth.isTimerActive = true;
      phoneAuth.remainingTime = 180;
      phoneAuth.buttonTimerText = `번호입력 ${phoneAuth.remainingTime.toFixed(1)}s`;
      phoneAuth.timerInterval = setInterval(() => {
        phoneAuth.remainingTime -= 0.1;
        if (phoneAuth.remainingTime <= 0) {
          stopTimer();
          phonePopupFields.info = '인증 시간이 만료되었습니다. 다시 시도해주세요.';
        } else {
          phoneAuth.buttonTimerText = `번호입력 ${phoneAuth.remainingTime.toFixed(1)}s`;
        }
      }, 100);
    };
    const stopTimer = () => {
      if (phoneAuth.timerInterval) clearInterval(phoneAuth.timerInterval);
      phoneAuth.isTimerActive = false;
      phoneAuth.remainingTime = 180;
    };
    const handleAuthCodeInput = (index, event) => {
      const value = event.target.value;
      if (!/^[0-9]*$/.test(value)) {
        event.target.value = phoneAuth.authCode[index];
        return;
      }
      phoneAuth.authCode[index] = value;
      if (value && index < phoneAuth.authCode.length - 1) {
        phoneAuth.authCodeInputs[index + 1]?.focus();
      }
      if (phoneAuth.authCode.every((digit) => digit !== '')) {
        verifyAuthCode();
      }
    };
    const verifyAuthCode = async () => {
      if (phoneAuth.isVerifyingCode) return;
      phoneAuth.isVerifyingCode = true;
      const code = phoneAuth.authCode.join('');
      const result = await window.apiService.postRequest('/user/verification/phone-verify-confirm', {
        type: 'phone',
        value: phonePopupFields.phone,
        code: code,
      });
      phonePopupFields.info = result.message || (result.success ? '인증 성공' : '인증 실패');
      phoneAuth.isPhoneVerified = result.success;
      if (result.success) {
        stopTimer();
        fields.phone = phonePopupFields.phone;
        closePhonePopup();
      }
      phoneAuth.isVerifyingCode = false;
    };

    // 주소 찾기(다음 우편번호)
    const openDaumPostcode = () => {
      const postcodeWrap = document.querySelector('.address-search-wrap');
      if (!postcodeWrap) return;
      if (typeof daum === 'undefined' || typeof daum.Postcode === 'undefined') {
        alert('주소 검색 서비스를 불러올 수 없습니다.');
        return;
      }
      postcodeWrap.style.display = 'block';
      postcodeWrap.innerHTML = '';
      isDaumPostcodeOpen.value = true;
      nextTick(() => {
        new daum.Postcode({
          oncomplete: (data) => {
            let addr = '';
            let extraAddr = '';
            if (data.userSelectedType === 'R') {
              addr = data.roadAddress;
            } else {
              addr = data.jibunAddress;
            }
            if (data.userSelectedType === 'R') {
              if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                extraAddr += data.bname;
              }
              if (data.buildingName !== '' && data.apartment === 'Y') {
                extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
              }
            }
            fields.address_postcode = data.zonecode;
            fields.address_base = addr;
            nextTick(() => {
              const detailInput = document.getElementById('address_detail');
              if (detailInput) detailInput.focus();
            });
            postcodeWrap.style.display = 'none';
            postcodeWrap.style.height = 'auto';
            isDaumPostcodeOpen.value = false;
          },
          onresize: (size) => {
            postcodeWrap.style.height = size.height + 'px';
          },
          width: '100%',
          height: '100%',
        }).embed(postcodeWrap, { autoClose: true });
      });
    };

    // 유효성 검사/제출 등 join.js 참고 (필드별 validate, setInfoMessage 등)
    // ... (생략, 실제 구현 시 join.js에서 복붙/수정)

    const checkPassword = async () => {
      console.log('[checkPassword] 입력값:', passwordInput.value);
      passwordError.value = '';
      isLoading.value = true;
      const result = await window.apiService.postRequest('/user/modify/password-check', { password: passwordInput.value });
      console.log('[checkPassword] 서버 응답:', result);
      isLoading.value = false;
      if (result.success && result.data && result.data.success) {
        isPasswordChecked.value = true;
        await fetchUserInfo();
      } else {
        passwordError.value = '비밀번호가 일치하지 않습니다.';
      }
    };

    const fetchUserInfo = async () => {
      isLoading.value = true;
      const result = await window.apiService.getRequest('/user/modify/info');
      isLoading.value = false;
      if (result.status === 401 || result.status === 403 || (result.message && result.message.includes('로그인'))) {
        window.location.href = '/user/login';
        return;
      }
      if (result.success && result.data && result.data.user) {
        const user = result.data.user;
        fields.loginId = user.login_id || '';
        fields.name = user.name || '';
        fields.nickname = user.nickname || '';
        fields.email = user.email || '';
        fields.birth = user.birth ? user.birth.replace(/-/g, '') : '';
        fields.gender = user.gender || 'N';
        fields.address_postcode = user.address_postcode || '';
        fields.address_base = user.address_base || '';
        fields.address_detail = user.address_detail || '';
        fields.agreement_age = user.agreement_age || false;
        fields.agreement_service = user.agreement_service || false;
        fields.agreement_privacy = user.agreement_privacy || false;
        fields.agreement_alba = user.agreement_alba || false;
        fields.agreement_marketing = user.agreement_marketing || false;
        fields.agreement_benefits = user.agreement_benefits || false;
        fields.phone = user.phone_number || user.phone || '';
        canChangeNickname.value = result.data.can_change_nickname !== false;
        if (!canChangeNickname.value && user.nickname_changed_at) {
          const changedAt = new Date(user.nickname_changed_at);
          const now = new Date();
          const diff = Math.floor((now - changedAt) / (1000 * 60 * 60 * 24));
          daysLeft.value = 90 - diff;
        } else {
          daysLeft.value = 0;
        }
      } else {
        serverErrors.general = '회원정보를 불러오지 못했습니다.';
      }
    };

    const submitModifyForm = async () => {
      isSubmitting.value = true;
      serverErrors.general = '';
      const payload = { ...fields };
      const result = await window.apiService.postRequest('/user/modify', payload);
      isSubmitting.value = false;
      if (result.success) {
        alert('회원정보가 성공적으로 수정되었습니다!');
        window.location.reload();
      } else {
        serverErrors.general = result.message || '회원정보 수정에 실패했습니다.';
      }
    };

    const checkLoginAndFetchUserInfo = async () => {
      try {
        const response = await fetch("/usercheck", { method: "GET" });
        const data = await response.json();
        if (!data.success) {
          window.location.href = "/user/login";
          return;
        }
        await fetchUserInfo();
      } catch (e) {
        window.location.href = "/user/login";
      }
    };

    onMounted(() => {
      isPasswordChecked.value = false;
      checkLoginAndFetchUserInfo();
    });

    return {
      isLoading,
      isSubmitting,
      isPasswordChecked,
      passwordInput,
      passwordError,
      serverErrors,
      fields,
      checkPassword,
      submitModifyForm,
      // 핸드폰 변경 팝업 관련
      showPhonePopup,
      phonePopupFields,
      phoneAuth,
      openPhonePopup,
      closePhonePopup,
      handlePhoneInput,
      sendVerificationCode,
      handleAuthCodeInput,
      verifyAuthCode,
      // 약관
      agreements,
      agreeAll,
      // 주소
      openDaumPostcode,
      isDaumPostcodeOpen,
      canChangeNickname,
      daysLeft,
    };
  },
});

modifyApp.mount('#modify-app');
