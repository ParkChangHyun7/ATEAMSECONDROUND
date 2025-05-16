function createFieldState(initialValue = "", additionalProps = {}) {
  return {
    value: initialValue,
    info: null,
    checking: false,
    valid: null,
    infoTimeoutId: null,
    ...additionalProps,
  };
}

const joinApp = Vue.createApp({
  data() {
    return {
      isSendingCode: false,
      showAuthCodeInputs: false,
      agreeAll: false,
      agreements: {
        age: false,
        service: false,
        privacy: false,
        alba: false,
        marketing: false,
        benefits: false,
      },
      phoneInputDebounceTimerId: null,
      buttonTimerText: "",
      isTimerActive: false,
      authCode: ["", "", "", "", ""],
      isVerifyingCode: false,
      isPhoneVerified: false,
      isVerificationSent: false,
      gender: "N",
      isSubmitting: false,
      serverErrors: {},
      fields: {
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
      },
      isDaumPostcodeOpen: false,
      timerInterval: null,
      remainingTime: 180,
    };
  },
  computed: {
    allRequiredAgreed() {
      return (
        this.agreements.age &&
        this.agreements.service &&
        this.agreements.privacy
      );
    },
    isCertificationButtonDisabled() {
      return (
        !this.fields.phone.valid || this.isSendingCode || this.isTimerActive
      );
    },
    canSubmitForm() {
      console.log("--- canSubmitForm 검사 시작 ---");
      console.log("isPhoneVerified:", this.isPhoneVerified);
      if (!this.isPhoneVerified) {
        console.log("검사 중단: 휴대폰 인증 미완료");
        return false;
      }
      console.log("isSubmitting:", this.isSubmitting);
      if (this.isSubmitting) {
        console.log("검사 중단: 제출 진행 중");
        return false;
      }

      const requiredFields = ["loginId", "password", "name", "nickname"];
      for (const fieldName of requiredFields) {
        console.log(`필드[${fieldName}].valid:`, this.fields[fieldName]?.valid);
        if (!this.fields[fieldName]?.valid) {
          console.log(`검사 중단: 필수 필드(${fieldName}) 유효성 실패`);
          return false;
        }
      }
      if (this.fields.email.value && !this.fields.email.valid) return false;
      if (this.fields.birth.value && !this.fields.birth.valid) return false;

      console.log("--- canSubmitForm 검사 통과 ---");
      return true;
    },
  },
  watch: {},
  methods: {
    toggleAllAgreements() {
      const checkStatus = this.agreeAll;
      for (const key in this.agreements) {
        this.agreements[key] = checkStatus;
      }
    },

    handlePhoneInput() {
      handlePhoneInputJS(this);
    },

    sendVerificationCode() {
      sendVerificationCodeJS(this);
    },

    checkIndividualAgreement() {
      this.agreeAll =
        this.agreements.age &&
        this.agreements.service &&
        this.agreements.privacy &&
        this.agreements.alba &&
        this.agreements.marketing &&
        this.agreements.benefits;
    },

    handleAuthCodeInput(index, event) {
      handleAuthCodeInputJS(this, index, event);
    },
    handleAuthCodeKeydown(index, event) {
      handleAuthCodeKeydownJS(this, index, event);
    },
    handleAuthCodePaste(event) {
      handleAuthCodePasteJS(this, event);
    },

    verifyAuthCode() {
      verifyAuthCodeJS(this);
    },

    setInfoMessage(fieldKey, message, duration = 5000) {
      let targetObject = null;
      let infoProp = null;
      let timeoutProp = null;

      if (this.fields[fieldKey]) {
        targetObject = this.fields[fieldKey];
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
    },

    async validateField(fieldName) {
      const field = this.fields[fieldName];
      if (!field.value) {
        return;
      }
      const noCheckField = ["email", "birth", "name"];

      if (!field || field.checking) {
        return;
      }

      if (noCheckField.includes(fieldName) && !field.value) {
        this.setInfoMessage(fieldName, null);
        field.valid = true;
        field.checking = false;
        return;
      }

      if (!field.value && fieldName) field.checking = true;
      if (fieldName === "birth" || fieldName === "name") {
        this.setInfoMessage(fieldName, "", 100);
      } else {
        this.setInfoMessage(fieldName, "확인 중...", 2000);
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
        this.setInfoMessage(
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
        this.setInfoMessage(
          fieldName,
          result.message ||
            (fieldName == "password"
              ? "사용 가능한 비밀번호 입니다."
              : `${valueToSend}는(은) 사용 가능합니다.`)
        );
        field.valid = true;
      } else {
        this.setInfoMessage(
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
    },

    handleLoginIdBlur() {
      this.validateField("loginId");
    },

    handlePasswordBlur() {
      this.validateField("password");
    },

    handleNicknameBlur() {
      this.validateField("nickname");
    },

    handleNameBlur() {
      this.validateField("name");
    },

    handleEmailBlur() {
      this.validateField("email");
    },

    handleBirthBlur() {
      this.validateField("birth");
    },

    startTimer() {
      console.log("Component startTimer called");
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
        this.timerInterval = null;
      }

      console.log("startTimer 호출됨");

      this.isTimerActive = true;
      this.remainingTime = 180;
      this.buttonTimerText = `번호입력 ${this.remainingTime.toFixed(1)}s`;

      this.timerInterval = setInterval(() => {
        this.remainingTime -= 0.1;
        if (this.remainingTime <= 0) {
          this.stopTimer();
          this.setInfoMessage(
            "phone",
            "인증 시간이 만료되었습니다. 다시 시도해주세요."
          );
        } else {
          this.buttonTimerText = `번호입력 ${this.remainingTime.toFixed(1)}s`;
        }
      }, 100);
    },

    stopTimer() {
      console.log("Component stopTimer called");
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
        this.timerInterval = null;
      }
      this.isTimerActive = false;
      this.remainingTime = 180;
    },

    openDaumPostcode() {
      const postcodeWrap = document.querySelector(".address-search-wrap");
      if (!postcodeWrap) {
        console.error(
          "주소 검색 영역(.address-search-wrap)을 찾을 수 없습니다."
        );
        return;
      }

      if (typeof daum === "undefined" || typeof daum.Postcode === "undefined") {
        console.error("Daum 우편번호 서비스 API가 로드되지 않았습니다.");
        this.setInfoMessage(
          "address_postcode",
          "주소 검색 서비스를 불러올 수 없습니다. 잠시 후 다시 시도해주세요."
        );
        return;
      }

      postcodeWrap.style.display = "block";
      postcodeWrap.innerHTML = "";
      this.isDaumPostcodeOpen = true;

      this.$nextTick(() => {
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

            this.fields.address_postcode.value = data.zonecode;
            this.fields.address_base.value = addr;

            this.$nextTick(() => {
              const detailInput = document.getElementById("address_detail");
              if (detailInput) detailInput.focus();
            });

            postcodeWrap.style.display = "none";
            postcodeWrap.style.height = "auto";
            this.isDaumPostcodeOpen = false;
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
    },

    async submitJoinForm() {
      console.log("submitJoinForm 호출됨");
      this.serverErrors = {};

      if (!this.canSubmitForm) {
        let alertMessage = "입력 정보를 확인해주세요.\n";
        if (!this.isPhoneVerified) {
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
        for (const key in this.fields) {
          if (fieldNames[key]) {
            if (
              (key === "email" || key === "birth") &&
              this.fields[key].value &&
              !this.fields[key].valid
            ) {
              alertMessage += `- ${fieldNames[key]} 형식이 올바르지 않습니다.\n`;
            } else if (
              key !== "email" &&
              key !== "birth" &&
              !this.fields[key].valid
            ) {
              alertMessage += `- ${fieldNames[key]}를 확인해주세요.\n`;
            }
          }
        }

        alert(alertMessage.trim());
        return;
      }

      this.isSubmitting = true;

      const requestBody = {
        login_id: this.fields.loginId.value,
        password: this.fields.password.value,
        name: this.fields.name.value,
        nickname: this.fields.nickname.value,
        email: this.fields.email.value,
        birth: this.fields.birth.value,
        phone_number: this.fields.phone.value,
        gender: this.gender,
        address_postcode: this.fields.address_postcode.value,
        address_base: this.fields.address_base.value,
        address_detail: this.fields.address_detail.value,
        agreement_age: this.agreements.age,
        agreement_service: this.agreements.service,
        agreement_privacy: this.agreements.privacy,
        agreement_alba: this.agreements.alba,
        agreement_marketing: this.agreements.marketing,
        agreement_benefits: this.agreements.benefits,
      };

      console.log("회원가입 요청 데이터:", requestBody);

      try {
        const result = await apiService.postRequest(
          "/joinConfirm",
          requestBody
        );
        console.log("회원가입 응답:", result);

        if (result.success && result.data?.success === true) {
          this.serverErrors = {};
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

                const vueFieldKey = Object.keys(this.fields).find(
                  (fKey) =>
                    fKey === key ||
                    (fKey === "loginId" && key === "login_id") ||
                    (fKey === "name" && key === "name") ||
                    (fKey === "phone" && key === "phone_number")
                );
                if (vueFieldKey) {
                  this.setInfoMessage(vueFieldKey, result.data[key]);
                  this.fields[vueFieldKey].valid = false;
                }
              }
            }
          }
          this.serverErrors = errorsFromServer;

          if (Object.keys(this.serverErrors).length === 0) {
            this.serverErrors = {
              general:
                result.message ||
                "회원가입 처리 중 알 수 없는 오류가 발생했습니다.",
            };
          }
        }
      } catch (error) {
        console.error("회원가입 API 호출 중 예외 발생:", error);
        this.serverErrors = {
          general:
            "회원가입 처리 중 오류가 발생했습니다. 네트워크 연결을 확인하거나 잠시 후 다시 시도해주세요.",
        };
      } finally {
        this.isSubmitting = false;
      }
    },
  },

  mounted() {
    console.log("Vue 인스턴스 mounted! 시점:", new Date().toLocaleTimeString());
  },
  beforeUnmount() {
    if (this.phoneInputDebounceTimerId) {
      clearTimeout(this.phoneInputDebounceTimerId);
    }
    for (const fieldName in this.fields) {
      const field = this.fields[fieldName];
      if (field.infoTimeoutId) {
        clearTimeout(field.infoTimeoutId);
      }
    }
    this.stopTimer();
  },
});
joinApp.mount("#join-app");
