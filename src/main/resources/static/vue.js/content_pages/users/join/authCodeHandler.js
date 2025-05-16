function handleAuthCodeInputJS(vm, index, event) {
  const value = event.target.value;
  if (!/^[0-9]*$/.test(value)) {
    event.target.value = vm.authCode[index];
    return;
  }
  vm.authCode[index] = value;

  if (value && index < vm.authCode.length - 1) {
    vm.$refs[`authCodeInput${index + 1}`]?.focus();
  }

  if (vm.authCode.every((digit) => digit !== "")) {
    vm.verifyAuthCode();
  }
}

function handleAuthCodeKeydownJS(vm, index, event) {
  if (event.key === "Backspace" && vm.authCode[index] === "" && index > 0) {
    vm.$refs[`authCodeInput${index - 1}`]?.focus();
  }
}

function handleAuthCodePasteJS(vm, event) {
  const pastedData = event.clipboardData.getData("text");
  if (!pastedData) return;

  const digits = pastedData.match(/^\d{5}$/);

  if (digits) {
    vm.authCode = digits[0].split("");
    vm.$nextTick(() => {
      vm.$refs[`authCodeInput${vm.authCode.length - 1}`]?.focus();
      vm.verifyAuthCode();
    });
  } else {
    vm.setInfoMessage("phoneInfo", "인증 코드를 확인 해주세요.");
  }
}
