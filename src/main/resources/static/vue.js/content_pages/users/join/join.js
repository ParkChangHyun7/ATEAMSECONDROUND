import { createApp, ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue';

const joinApp = createApp({
  setup() {
    const isSendingCode = ref(false);
    const showAuthCodeInputs = ref(false);
    const agreeAll = ref(false);
    const agreements = reactive({
      age: false,
      service: false,
      privacy: false,
      alba: false,
      marketing: false,
      benefits: false,
    });

    const phoneInputDebounceTimerId = ref(null);
    const buttonTimerText = ref("");
    const isTimerActive = ref(false);
    const authCode = reactive(["", "", "", "", ""]);
    const isVerifyingCode = ref(false);
    const isPhoneVerified = ref(false);
    const isVerificationSent = ref(false);
    const gender = ref("N");
    const isSubmitting = ref(false);
    const serverErrors = reactive({});

    const createFieldState = (initialValue = "", additionalProps = {}) => reactive({
      value: initialValue,
      info: null,
      checking: false,
      valid: null,
      infoTimeoutId: null,
      ...additionalProps,
    });

    const fields = reactive({
      loginId: createFieldState(),
      password: createFieldState(),
      name: createFieldState(),
      nickname: createFieldState(),
      email: createFieldState(),
      birth: createFieldState(),
      phone: createFieldState("", { formattedValue: "" }),
      address_postcode: createFieldState(),
      address_base: createFieldState(),
      address_detail: createFieldState(),
    });

    const isDaumPostcodeOpen = ref(false);
    const timerInterval = ref(null);
    const remainingTime = ref(180);

    const authCodeInputs = ref([]);

    const allRequiredAgreed = computed(() => {
      return (
        agreements.age &&
        agreements.service &&
        agreements.privacy
      );
    });

    const isCertificationButtonDisabled = computed(() => {
      return (
        !fields.phone.valid || isSendingCode.value || isTimerActive.value
      );
    });

    const canSubmitForm = computed(() => {
      console.log("--- canSubmitForm 검사 시작 ---");
      console.log("isPhoneVerified:", isPhoneVerified.value);
      if (!isPhoneVerified.value) {
        console.log("검사 중단: 휴대폰 인증 미완료");
        return false;
      }
      console.log("isSubmitting:", isSubmitting.value);
      if (isSubmitting.value) {
        console.log("검사 중단: 제출 진행 중");
        return false;
      }

      const requiredFields = ["loginId", "password", "name", "nickname"];
      for (const fieldName of requiredFields) {
        console.log(`필드[${fieldName}].valid:`, fields[fieldName]?.valid);
        if (!fields[fieldName]?.valid) {
          console.log(`검사 중단: 필수 필드(${fieldName}) 유효성 실패`);
          return false;
        }
      }
      if (fields.email.value && !fields.email.valid) return false;
      if (fields.birth.value && !fields.birth.valid) return false;

      console.log("--- canSubmitForm 검사 통과 ---");
      return true;
    });

    const toggleAllAgreements = () => {
      const checkStatus = agreeAll.value;
      for (const key in agreements) {
        agreements[key] = checkStatus;
      }
    };

    const checkIndividualAgreement = () => {
      agreeAll.value =
        agreements.age &&
        agreements.service &&
        agreements.privacy &&
        agreements.alba &&
        agreements.marketing &&
        agreements.benefits;
    };

    const setInfoMessage = (fieldKey, message, duration = 5000) => {
      let targetObject = null;
      let infoProp = null;
      let timeoutProp = null;

      if (fields[fieldKey]) {
        targetObject = fields[fieldKey];
        infoProp = "info";
        timeoutProp = "infoTimeoutId";
      } else {
        console.error("setInfoMessage에 필요한 키가 아닙니다.:", fieldKey);
        return;
      }

      if (targetObject[timeoutProp]) {
        clearTimeout(targetObject[timeoutProp]);
      }

      targetObject[infoProp] = message;

      if (message !== null && duration > 0) {
        targetObject[timeoutProp] = setTimeout(() => {
          targetObject[infoProp] = null;
          targetObject[timeoutProp] = null;
        }, duration);
      }
    };

    const validateField = async (fieldName) => {
      const field = fields[fieldName];
      if (!field.value) {
        return;
      }
      const noCheckField = ["email", "birth", "name"];

      if (!field || field.checking) {
        return;
      }

      if (noCheckField.includes(fieldName) && !field.value) {
        setInfoMessage(fieldName, null);
        field.valid = true;
        field.checking = false;
        return;
      }

      if (!field.value && fieldName) field.checking = true;
      if (fieldName === "birth" || fieldName === "name") {
        setInfoMessage(fieldName, "", 100);
      } else {
        setInfoMessage(fieldName, "확인 중...", 2000);
      }
      const endPoint = "/user/validation/dupl-check";

      const bodyKeyMap = {
        loginId: "login_id",
        name: "name",
        nickname: "nickname",
        email: "email",
        phone: "phone_number",
        password: "password",
        birth: "birth",
        gender: "gender",
      };

      const bodyKey = bodyKeyMap[fieldName];

      if (!endPoint || !bodyKey) {
        console.error(
          "엔드포인트 또는 바디 키가 설정되지 않았습니다:",
          fieldName
        );
        setInfoMessage(
          fieldName,
          "오류가 발생하였습니다. <br>반복 될 시 관리자에게 문의하세요."
        );
        field.checking = false;
        field.valid = false;
        return;
      }

      const valueToSend =
        fieldName === "phone" && field.formattedValue
          ? field.formattedValue
          : field.value;

      const requiredData = {
        type: bodyKey,
        value: valueToSend,
      };

      const result = await apiService.postRequest(endPoint, requiredData);
      if (result.success) {
        setInfoMessage(
          fieldName,
          result.message ||
            (fieldName == "password"
              ? "사용 가능한 비밀번호 입니다."
              : `${valueToSend}는(은) 사용 가능합니다.`)
        );
        field.valid = true;
      } else {
        setInfoMessage(
          fieldName,
          result.message || `${valueToSend}는(은) 사용할 수 없습니다.`
        );
        field.valid = false;

        if (result.data) {
          console.log(
            `${fieldName} 유효성 검사 실패. 서버 응답 본문:`,
            result.data
          );
        } else {
          console.log(`${fieldName} 유효성 검사 실패. 추가 본문 데이터 없음.`);
        }
      }
      field.checking = false;
    };

    const getFormattedPhoneNumber = () => {
      const field = fields.phone;
      const phoneNumberTrimmed = field.value.trim();
      const regex1 = /^010-\d{4}-\d{4}$/;
      const regex2 = /^010\d{8}$/;

      if (regex1.test(phoneNumberTrimmed)) {
        field.formattedValue = phoneNumberTrimmed.replace(/-/g, "");
        return field.formattedValue;
      } else if (regex2.test(phoneNumberTrimmed)) {
        field.formattedValue = phoneNumberTrimmed;
        return field.formattedValue;
      } else {
        field.formattedValue = "";
        return null;
      }
    };

    const startTimer = () => {
      console.log("Component startTimer called");
      if (timerInterval.value) {
        clearInterval(timerInterval.value);
        timerInterval.value = null;
      }

      console.log("startTimer 호출됨");

      isTimerActive.value = true;
      remainingTime.value = 180;
      buttonTimerText.value = `번호입력 ${remainingTime.value.toFixed(1)}s`;

      timerInterval.value = setInterval(() => {
        remainingTime.value -= 0.1;
        if (remainingTime.value <= 0) {
          stopTimer();
          setInfoMessage(
            "phone",
            "인증 시간이 만료되었습니다. 다시 시도해주세요."
          );
        } else {
          buttonTimerText.value = `번호입력 ${remainingTime.value.toFixed(1)}s`;
        }
      }, 100);
    };

    const stopTimer = () => {
      console.log("Component stopTimer called");
      if (timerInterval.value) {
        clearInterval(timerInterval.value);
        timerInterval.value = null;
      }
      isTimerActive.value = false;
      remainingTime.value = 180;
    };

    const handlePhoneInput = () => {
      const phoneNum = fields.phone.value;
      const numberOnly = phoneNum.replace(/[^0-9]/g, "");
      if (phoneNum !== numberOnly) {
        fields.phone.value = numberOnly;
      }
      if (phoneInputDebounceTimerId.value) {
        clearTimeout(phoneInputDebounceTimerId.value);
      }
      phoneInputDebounceTimerId.value = setTimeout(() => {
        validateField("phone");
      }, 300);
    };

    const sendVerificationCode = async () => {
      const phoneField = fields.phone;
      if (isCertificationButtonDisabled.value || !phoneField.valid) return;

      const formattedPhoneNumber = getFormattedPhoneNumber();
      if (!formattedPhoneNumber) {
        setInfoMessage("phone", "유효한 휴대폰 번호 형식이 아닙니다.");
        return;
      }

      isSendingCode.value = true;
      setInfoMessage("phone", null);

      const result = await apiService.postRequest(
        "/user/verification/phone-verify-send",
        {
          type: "phone",
          value: formattedPhoneNumber,
        }
      );

      if (result.success) {
        isVerificationSent.value = true;
        showAuthCodeInputs.value = true;
        startTimer();
      } else {
        setInfoMessage(
          "phone",
          result.message || "인증번호 전송에 실패했습니다."
        );
      }
      isSendingCode.value = false;
    };

    const handleAuthCodeInput = (index, event) => {
      const value = event.target.value;
      if (!/^[0-9]*$/.test(value)) {
        event.target.value = authCode[index];
        return;
      }
      authCode[index] = value;

      if (value && index < authCode.length - 1) {
        authCodeInputs.value[index + 1]?.focus();
      }

      if (authCode.every((digit) => digit !== "")) {
        verifyAuthCode();
      }
    };

    const handleAuthCodeKeydown = (index, event) => {
      if (event.key === "Backspace" && authCode[index] === "" && index > 0) {
        authCodeInputs.value[index - 1]?.focus();
      }
    };

    const handleAuthCodePaste = (event) => {
      const pastedData = event.clipboardData.getData("text");
      if (!pastedData) return;

      const digits = pastedData.match(/^\d{5}$/);

      if (digits) {
        authCode.splice(0, authCode.length, ...digits[0].split(""));
        nextTick(() => {
          authCodeInputs.value[authCode.length - 1]?.focus();
          verifyAuthCode();
        });
      } else {
        setInfoMessage("phone", "인증 코드를 확인 해주세요.");
      }
    };

    const verifyAuthCode = async () => {
      if (isVerifyingCode.value) return;
      isVerifyingCode.value = true;
      setInfoMessage("phone", null);

      const verificationCode = authCode.join("");
      const formattedPhoneNumber = fields.phone.formattedValue;

      if (!formattedPhoneNumber) {
        setInfoMessage("phone", "휴대폰 번호가 유효하지 않습니다.");
        isPhoneVerified.value = false;
        isVerifyingCode.value = false;
        return;
      }

      console.log(
        `인증번호 검증 요청: ${formattedPhoneNumber}, 코드: ${verificationCode}`
      );

      const result = await apiService.postRequest(
        "/user/verification/phone-verify-confirm",
        {
          type: "phone",
          value: formattedPhoneNumber,
          code: verificationCode,
        }
      );

      setInfoMessage(
        "phone",
        result.message || (result.success ? "인증 성공" : "인증 실패")
      );
      isPhoneVerified.value = result.success;

      if (result.success) {
        stopTimer();
        nextTick(() => {
          const secondPage = document.querySelector(".second-page-wrap");
          if (secondPage) {
            const styles = window.getComputedStyle(secondPage);
          }
        });
      }
      isVerifyingCode.value = false;
    };

    const openDaumPostcode = () => {
      const postcodeWrap = document.querySelector(".address-search-wrap");
      if (!postcodeWrap) {
        console.error(
          "주소 검색 영역(.address-search-wrap)을 찾을 수 없습니다."
        );
        return;
      }

      if (typeof daum === "undefined" || typeof daum.Postcode === "undefined") {
        console.error("Daum 우편번호 서비스 API가 로드되지 않았습니다.");
        setInfoMessage(
          "address_postcode",
          "주소 검색 서비스를 불러올 수 없습니다. 잠시 후 다시 시도해주세요."
        );
        return;
      }

      postcodeWrap.style.display = "block";
      postcodeWrap.innerHTML = "";
      isDaumPostcodeOpen.value = true;

      nextTick(() => {
        new daum.Postcode({
          oncomplete: (data) => {
            let addr = "";
            let extraAddr = "";

            if (data.userSelectedType === "R") {
              addr = data.roadAddress;
            } else {
              addr = data.jibunAddress;
            }

            if (data.userSelectedType === "R") {
              if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
                extraAddr += data.bname;
              }
              if (data.buildingName !== "" && data.apartment === "Y") {
                extraAddr +=
                  extraAddr !== ""
                    ? ", " + data.buildingName
                    : data.buildingName;
              }
            }

            fields.address_postcode.value = data.zonecode;
            fields.address_base.value = addr;

            nextTick(() => {
              const detailInput = document.getElementById("address_detail");
              if (detailInput) detailInput.focus();
            });

            postcodeWrap.style.display = "none";
            postcodeWrap.style.height = "auto";
            isDaumPostcodeOpen.value = false;
          },
          onresize: (size) => {
            postcodeWrap.style.height = size.height + "px";
          },
          width: "100%",
          height: "100%",
        }).embed(postcodeWrap, {
          autoClose: true,
        });
      });
    };

    const submitJoinForm = async () => {
      console.log("submitJoinForm 호출됨");
      serverErrors = {};

      if (!canSubmitForm.value) {
        let alertMessage = "입력 정보를 확인해주세요.\n";
        if (!isPhoneVerified.value) {
          alertMessage += "- 휴대폰 인증이 완료되지 않았습니다.\n";
        }
        const fieldNames = {
          loginId: "아이디",
          password: "비밀번호",
          name: "이름",
          nickname: "닉네임",
          email: "이메일",
          birth: "생년월일",
        };
        for (const key in fields) {
          if (fieldNames[key]) {
            if (
              (key === "email" || key === "birth") &&
              fields[key].value &&
              !fields[key].valid
            ) {
              alertMessage += `- ${fieldNames[key]} 형식이 올바르지 않습니다.\n`;
            } else if (
              key !== "email" &&
              key !== "birth" &&
              !fields[key].valid
            ) {
              alertMessage += `- ${fieldNames[key]}를 확인해주세요.\n`;
            }
          }
        }

        alert(alertMessage.trim());
        return;
      }

      isSubmitting.value = true;

      const requestBody = {
        login_id: fields.loginId.value,
        password: fields.password.value,
        name: fields.name.value,
        nickname: fields.nickname.value,
        email: fields.email.value,
        birth: fields.birth.value,
        phone_number: fields.phone.value,
        gender: gender.value,
        address_postcode: fields.address_postcode.value,
        address_base: fields.address_base.value,
        address_detail: fields.address_detail.value,
        agreement_age: agreements.age,
        agreement_service: agreements.service,
        agreement_privacy: agreements.privacy,
        agreement_alba: agreements.alba,
        agreement_marketing: agreements.marketing,
        agreement_benefits: agreements.benefits,
      };

      console.log("회원가입 요청 데이터:", requestBody);

      try {
        const result = await apiService.postRequest(
          "/joinConfirm",
          requestBody
        );
        console.log("회원가입 응답:", result);

        if (result.success && result.data?.success === true) {
          serverErrors = {};
          alert(
            "회원가입이 성공적으로 완료되었습니다! 로그인 페이지로 이동합니다."
          );
          window.location.href = "/user/login";
        } else {
          const errorsFromServer = {};
          if (result.data) {
            for (const key in result.data) {
              if (
                key !== "success" &&
                key !== "finally" &&
                typeof result.data[key] === "string"
              ) {
                errorsFromServer[key] = result.data[key];

                const vueFieldKey = Object.keys(fields).find(
                  (fKey) =>
                    fKey === key ||
                    (fKey === "loginId" && key === "login_id") ||
                    (fKey === "name" && key === "name") ||
                    (fKey === "phone" && key === "phone_number")
                );
                if (vueFieldKey) {
                  setInfoMessage(vueFieldKey, result.data[key]);
                  fields[vueFieldKey].valid = false;
                }
              }
            }
          }
          serverErrors = errorsFromServer;

          if (Object.keys(serverErrors).length === 0) {
            serverErrors.general =
                result.message ||
                "회원가입 처리 중 알 수 없는 오류가 발생했습니다.";
          }
        }
      } catch (error) {
        console.error("회원가입 API 호출 중 예외 발생:", error);
        serverErrors.general =
            "회원가입 처리 중 오류가 발생했습니다. 네트워크 연결을 확인하거나 잠시 후 다시 시도해주세요.";
      } finally {
        isSubmitting.value = false;
      }
    };

    onMounted(() => {
      console.log("Vue 인스턴스 mounted! 시점:", new Date().toLocaleTimeString());
    });

    onBeforeUnmount(() => {
      if (phoneInputDebounceTimerId.value) {
        clearTimeout(phoneInputDebounceTimerId.value);
      }
      for (const fieldName in fields) {
        const field = fields[fieldName];
        if (field.infoTimeoutId) {
          clearTimeout(field.infoTimeoutId);
        }
      }
      stopTimer();
    });

    watch(agreeAll, (newValue) => {
      for (const key in agreements) {
        agreements[key] = newValue;
      }
    });

    return {
      isSendingCode,
      showAuthCodeInputs,
      agreeAll,
      agreements,
      buttonTimerText,
      isTimerActive,
      authCode,
      isVerifyingCode,
      isPhoneVerified,
      isVerificationSent,
      gender,
      isSubmitting,
      serverErrors,
      fields,
      isDaumPostcodeOpen,
      authCodeInputs,
      allRequiredAgreed,
      isCertificationButtonDisabled,
      canSubmitForm,
      toggleAllAgreements,
      handlePhoneInput,
      sendVerificationCode,
      checkIndividualAgreement,
      handleAuthCodeInput,
      handleAuthCodeKeydown,
      handleAuthCodePaste,
      verifyAuthCode,
      setInfoMessage,
      validateField,
      openDaumPostcode,
      submitJoinForm,
    };
  },
});

joinApp.mount("#join-app");
