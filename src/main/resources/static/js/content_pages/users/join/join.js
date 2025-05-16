const agreeAllCheckbox = document.getElementById("agree-all");
const individualCheckboxes = document.querySelectorAll(
  '.agreement-item input[type="checkbox"]:not(#agree-all)'
);
const getCsrfHeaders = () => {
  const tokenElement = document.querySelector('meta[name="_csrf"]');
  const headerElement = document.querySelector('meta[name="_csrf_header"]');

  if (tokenElement && headerElement) {
    const token = tokenElement.getAttribute("content");
    const header = headerElement.getAttribute("content");
    if (token && header) {
      return { [header]: token };
    }
  }
  console.error("CSRF 메타 태그를 찾을 수 없거나 비어있습니다.");
  return null;
};
agreeAllCheckbox.addEventListener("change", function () {
  individualCheckboxes.forEach((checkbox) => {
    checkbox.checked = this.checked;
  });
});

individualCheckboxes.forEach((checkbox) => {
  checkbox.addEventListener("change", function () {
    if (!this.checked) {
      agreeAllCheckbox.checked = false;
    } else {
      let allChecked = true;
      individualCheckboxes.forEach((cb) => {
        if (cb.required && !cb.checked) {
          allChecked = false;
        }
      });
      let allRequiredChecked = true;
      document
        .querySelectorAll('.agreement-item input[type="checkbox"][required]')
        .forEach((reqCb) => {
          if (!reqCb.checked) {
            allRequiredChecked = false;
          }
        });
      agreeAllCheckbox.checked = allRequiredChecked && allChecked;
    }
  });
});

// --- 휴대폰 번호 관련 스크립트 시작 ---
const phoneInput = document.querySelector(".phone-input");
const phoneInfo = document.querySelector(".phone-info");
const verifyButton = document.querySelector(".phone-verify-button");
const authCodeInputsContainer = document.querySelector(
  ".phone-authcode-inputs"
);

phoneInput.addEventListener("blur", async () => {
  const phoneNumber = phoneInput.value.trim();
  const regex1 = /^010-\d{4}-\d{4}$/; // 010-1234-1234 형식
  const regex2 = /^010\d{8}$/; // 01012341234 형식
  let formattedPhoneNumber = "";

  phoneInfo.innerHTML = ""; // 이전 메시지 초기화
  authCodeInputsContainer.style.display = "none"; // 인증번호 입력칸 숨김
  verifyButton.disabled = true; // 인증번호 전송 버튼 비활성화

  if (regex1.test(phoneNumber)) {
    formattedPhoneNumber = phoneNumber.replace(/-/g, "");
  } else if (regex2.test(phoneNumber)) {
    formattedPhoneNumber = phoneNumber;
  } else {
    phoneInfo.innerHTML = "<span>휴대폰 번호 형식에 적합하지 않습니다.</span>";
    return; // 형식 오류 시 중복 검사 불필요
  }

  // 서버에 중복 검증 요청 (실제 구현 시 URL과 방식 확인 필요)
  try {
    // '/check-phone' 엔드포인트는 예시이며, 실제 프로젝트의 엔드포인트로 변경해야 합니다.
    // fetch API 대신 다른 AJAX 라이브러리(jQuery 등)를 사용할 수도 있습니다.
    const response = await fetch("/check-phone", {
      method: "POST", // 또는 GET, 서버 구현에 따라 결정
      headers: {
        "Content-Type": "application/json",
        // 필요 시 CSRF 토큰 등 추가 헤더 설정
      },
      body: JSON.stringify({ phone_number: formattedPhoneNumber }),
    });

    // 서버 응답 상태 확인 (성공: 2xx)
    if (!response.ok) {
      // 서버에서 에러 응답 시 (예: 409 Conflict - 이미 사용 중)
      const errorData = await response.json(); // 서버에서 보내는 에러 메시지 확인
      phoneInfo.innerHTML = `<span>${
        errorData.message || "이미 사용 중인 휴대폰 번호입니다."
      }</span>`;
    } else {
      // 중복 검증 성공 시
      verifyButton.disabled = false; // 인증번호 전송 버튼 활성화
      // 인증번호 전송 버튼 클릭 시 인증번호 입력칸 표시 로직 추가 필요
      // 우선 형식 및 중복 검증 성공 시 인증번호 입력칸 바로 표시 (요청사항 반영)
      authCodeInputsContainer.style.display = "inline-block";
    }
  } catch (error) {
    console.error("Error checking phone number:", error);
    phoneInfo.innerHTML =
      "<span>휴대폰 번호 검증 중 오류가 발생했습니다.</span>";
  }
});

// 인증번호 전송 버튼 클릭 이벤트 (추가 구현 필요)
verifyButton.addEventListener("click", () => {
  // 서버에 인증번호 전송 요청 로직
  console.log("인증번호 전송 요청");
  // 성공 시 phone-authcode-inputs 표시 (이미 blur 이벤트에서 처리됨)
  // 필요 시 여기서 추가 로직 구현 (예: 타이머 시작)
});

// 인증번호 입력 관련 로직 (추가 구현 필요)
const authCodeInputs = document.querySelectorAll(".phone-authcode");
authCodeInputs.forEach((input, index) => {
  input.addEventListener("input", (e) => {
    // 입력 시 다음 칸으로 자동 이동 등 UX 개선 로직
    if (input.value.length === 1 && index < authCodeInputs.length - 1) {
      authCodeInputs[index + 1].focus();
    }
    // 모든 칸이 채워졌는지 확인하고 확인 버튼 활성화 또는 자동 확인 로직
  });

  input.addEventListener("keydown", (e) => {
    // Backspace 키 처리 등
    if (e.key === "Backspace" && input.value.length === 0 && index > 0) {
      authCodeInputs[index - 1].focus();
    }
  });
});
// --- 휴대폰 번호 관련 스크립트 끝 ---
