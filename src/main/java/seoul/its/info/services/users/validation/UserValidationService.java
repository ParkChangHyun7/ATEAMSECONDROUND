package seoul.its.info.services.users.validation;

import org.springframework.validation.annotation.Validated;
// import jakarta.validation.Valid; // 더 이상 DTO를 직접 검증하지 않음
import java.util.Map;
import seoul.its.info.common.exception.BusinessException;

@Validated
public interface UserValidationService {
    Map<String, Object> duplicationChecker(String type, String value);

    boolean checkAnyDuplications(String type, String value);

    Map<String, Object> isValidAgeRange(String birthDate);

    Map<String, Object> validateBirthDate(String value);

    Map<String, Object> lastChecker(UserValidationDto dto);

    Map<String, Object> validateSingleField(String type, String value) throws BusinessException;
}