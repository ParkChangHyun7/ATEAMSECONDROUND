package seoul.its.info.services.users.validation.email;

import seoul.its.info.common.exception.BusinessException;

public interface EmailVerificationService {

    void sendVerificationEmail(String email, String code) throws BusinessException;

}