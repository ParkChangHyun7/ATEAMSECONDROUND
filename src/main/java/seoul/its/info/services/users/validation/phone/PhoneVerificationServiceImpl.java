package seoul.its.info.services.users.validation.phone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class PhoneVerificationServiceImpl implements PhoneVerificationService {

    private static final int CODE_LENGTH = 5;

    // application.properties 또는 application.yml 에서 설정값 주입
    @Value("${autohotkey.script.path}")
    private String autohotkeyScriptPath;

    @Value("${autohotkey.executable.path}")
    private String autohotkeyExecutablePath; // AutoHotkey.exe 경로 (선택 사항, PATH에 없으면 지정)

    @Override
    public void sendVerificationCode(String phoneNumber, String code) throws IOException, InterruptedException {
        System.out.println("===== AutoHotkey 스크립트 실행 시도 ====");
        System.out.println("스크립트 경로: " + autohotkeyScriptPath);
        System.out.println("대상 번호: " + phoneNumber);
        System.out.println("인증 코드: " + code);
        System.out.println("======================================");

        // ProcessBuilder를 사용하여 오토핫키 스크립트 실행
        // ProcessBuilder pb;
        // if (autohotkeyExecutablePath != null && !autohotkeyExecutablePath.isEmpty()) {
        //     pb = new ProcessBuilder(autohotkeyExecutablePath, autohotkeyScriptPath, phoneNumber, code);
        // } else {
        //     // 시스템 PATH에 AutoHotkey.exe가 설정되어 있다고 가정
        //     pb = new ProcessBuilder("AutoHotkey.exe", autohotkeyScriptPath, phoneNumber, code);
        // }

        // 표준 출력/오류를 리다이렉트하지 않으면 프로세스가 멈출 수 있음 (선택 사항)
        // pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        // pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        // Process process = pb.start();

        // 스크립트가 완료될 때까지 기다리지 않음 (비동기 실행)
        // 필요한 경우 process.waitFor()를 사용하되, 타임아웃 설정 고려

        System.out.println("AutoHotkey 스크립트 실행 요청 완료.");
    }

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}