package seoul.its.info.common.security.aesencryptor.AESKeyProvider;

public interface KeyDecodeChain {
    
    int getKeyLength();

    
    String getAlgorithmName();

    
    int getChainDepth();

    
    long getPreviousKeyHash();

    // 가상화 클래스를 이용하여 독립된 SandBox 환경으로 메서드를
    // 실행해서 키 디코드 체인의 루트를 찾아내고, 그 루트를 이용해서
    // 미들 체인의 엔드 포인트에서 결과를 반환 받아 키 디코드 체인
    // 완성에 기여함. 미들 체인의 50%는 더미 형태로 존재하기 때문에
    // 실제 키 디코드 체인의 루트를 찾아내는 것은 매우 어려움. (사실상 불가능)
    // 구조를 정확히 아는 모듈 설계자 이외에는 불가능하여
    // 모듈 설계자 부재시 AES 키 값을 찾는 난이도가 매우 높아짐.
    // 더미 체인과 액츄얼 체인을 구분하여 더미 체인의 잘못된 루트를
    // 제외하고 Bottom to Top 5 steps chain Pyramids 구조의 Path를
    // 구성하여 체인 완성에 기여해야 하기에, 더미 구분이 암호화의 핵심 킥임.
    // 보안 모듈에만 예외적으로 명시적 기능과 실제 기능을 분리하여
    // 공격자의 가독성을 어렵게 하고, 보안 모듈의 복잡도를 올림.

} 