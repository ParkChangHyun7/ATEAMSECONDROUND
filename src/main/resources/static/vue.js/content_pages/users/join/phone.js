function handlePhoneInputJS(vm) {
  const phoneNum = vm.fields.phone.value;
  const numberOnly = phoneNum.replace(/[^0-9]/g, "");
  if (phoneNum !== numberOnly) {
    vm.fields.phone.value = numberOnly;
  }
  if (vm.phoneInputDebounceTimerId) {
    clearTimeout(vm.phoneInputDebounceTimerId);
  }
  vm.phoneInputDebounceTimerId = setTimeout(() => {
    vm.validateField("phone");
  }, 300);
}

function getFormattedPhoneNumberJS(vm) {
  const field = vm.fields.phone;
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
}

async function sendVerificationCodeJS(vm) {
  const phoneField = vm.fields.phone;
  if (vm.isCertificationButtonDisabled || !phoneField.valid) return;

  const formattedPhoneNumber = getFormattedPhoneNumberJS(vm);
  if (!formattedPhoneNumber) {
    vm.setInfoMessage("phone", "유효한 휴대폰 번호 형식이 아닙니다.");
    return;
  }

  vm.isSendingCode = true;
  vm.setInfoMessage("phone", null);

  const result = await apiService.postRequest(
    "/user/verification/phone-verify-send",
    {
      type: "phone",
      value: formattedPhoneNumber,
    }
  );

  if (result.success) {
    vm.isVerificationSent = true;
    vm.showAuthCodeInputs = true;
    vm.startTimer();
  } else {
    vm.setInfoMessage(
      "phone",
      result.message || "인증번호 전송에 실패했습니다."
    );
  }
  vm.isSendingCode = false;
}

async function verifyAuthCodeJS(vm) {
  if (vm.isVerifyingCode) return;
  vm.isVerifyingCode = true;
  vm.setInfoMessage("phone", null);

  const verificationCode = vm.authCode.join("");
  const formattedPhoneNumber = vm.fields.phone.formattedValue;

  if (!formattedPhoneNumber) {
    vm.setInfoMessage("phone", "휴대폰 번호가 유효하지 않습니다.");
    vm.isPhoneVerified = false;
    vm.isVerifyingCode = false;
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

  vm.setInfoMessage(
    "phone",
    result.message || (result.success ? "인증 성공" : "인증 실패")
  );
  vm.isPhoneVerified = result.success;

  if (result.success) {
    vm.stopTimer();
    vm.$nextTick(() => {
      const secondPage = document.querySelector(".second-page-wrap");
      if (secondPage) {
        const styles = window.getComputedStyle(secondPage);
      }
    });
  }
  vm.isVerifyingCode = false;
}
