package seoul.its.info.services.users.validation.phone;

public interface PhoneVerificationService {
    void sendVerificationCode(String phoneNumber, String code) throws Exception;
    String generateVerificationCode();
} 