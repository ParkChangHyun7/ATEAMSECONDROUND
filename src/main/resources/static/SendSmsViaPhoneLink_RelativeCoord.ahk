#Requires AutoHotkey v2.0+ ; AutoHotkey v2 이상 필요

; --- 1. 인자 값 받기 ---
if (A_Args.Length < 2) {
    MsgBox("오류: 전화번호와 인증 코드가 필요합니다.`n사용법: SendSmsViaPhoneLink_RelativeCoord.ahk <전화번호> <인증코드>")
    ExitApp
}
targetPhoneNumber := A_Args[1]
verificationCode := A_Args[2]
messageBody := "서울특별시 교통정보센터 입니다. 인증번호는 [" verificationCode "]입니다. 보안에 주의하세요."

; --- 2. 대상 창 식별 정보 설정 ---
phoneLinkWinTitle := "휴대폰과 연결"
phoneLinkWinClass := "ahk_class WinUIDesktopWin32WindowClass"
phoneLinkWinExe := "ahk_exe PhoneExperienceHost.exe"
targetWinSpec := phoneLinkWinTitle . " " . phoneLinkWinClass . " " . phoneLinkWinExe

; --- 3. 창 활성화 및 크기/위치 고정 ---
if !WinExist(targetWinSpec) {
    MsgBox("오류: '휴대폰과 연결' 앱(" phoneLinkWinExe ")이 실행 중이지 않거나 창을 찾을 수 없습니다.")
    ExitApp
}
WinActivate(targetWinSpec)
if !WinWaitActive(targetWinSpec, , 3) { ; 3초간 활성화 대기
    MsgBox("오류: '휴대폰과 연결' 창을 활성화할 수 없습니다.")
    ExitApp
}

; 창 크기를 1280x750으로 조정하고 화면 좌상단(0,0)으로 이동 (위치는 조절 가능)
WinMove(0, 0, 1280, 750, targetWinSpec)
Sleep 500 ; 창 크기 변경 시간 대기

; --- 4. 좌표 모드 설정: 창의 클라이언트 영역 기준 ---
CoordMode "Mouse", "Client"

; --- 5. 지정된 상대 좌표 클릭 및 입력 ---

; 메시지 탭 클릭
Click 125, 75
Sleep 1000

; SMS 작성 버튼 클릭
Click 500, 190
Sleep 1000

; 받는 사람 입력란 클릭
Click 630, 180
Sleep 1000

; 핸드폰 번호 입력
SendInput targetPhoneNumber
Sleep 1000
SendInput "{Enter}" ; 엔터 입력
Sleep 1000

; 메시지 보내기 입력란 클릭
Click 630, 650
Sleep 1000

; 메시지 내용 입력
SendInput messageBody
Sleep 1000 ; 입력 시간 약간 대기

; 전송 버튼 클릭
Click 1175, 700
Sleep 500 ; 클릭 후 약간 대기

; --- 6. 스크립트 종료 ---
; MsgBox("SMS 전송 시도 완료 (좌표 기반): " . targetPhoneNumber) ; 확인 메시지 (선택 사항)
ExitApp