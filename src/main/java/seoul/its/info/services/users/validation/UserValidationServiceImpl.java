package seoul.its.info.services.users.validation;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.its.info.common.exception.BusinessException;
import seoul.its.info.common.security.aesencryptor.AESEncoderDecoder;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private final UserValidationMapper validationMapper;
    private final Validator validator;
    private final AESEncoderDecoder aesEncoderDecoder;

    @Autowired
    private final BadWordsLibrary badWordsLibrary;

    @Override
    public Map<String, Object> validateSingleField(String type, String value) throws BusinessException {
        log.info("validateSingleField 호출됨: type={}, value={}", type, value);

        String fieldName;
        Class<?> validationGroup;

        switch (type.toLowerCase()) {
            case "login_id":
                fieldName = "login_id";
                validationGroup = UserValidationDto.IdValidation.class;
                break;
            case "password":
                fieldName = "password";
                validationGroup = UserValidationDto.PasswordValidation.class;
                break;
            case "name":
                fieldName = "name";
                validationGroup = UserValidationDto.UserNameValidation.class;
                break;
            case "nickname":
                fieldName = "nickname";
                validationGroup = UserValidationDto.NicknameValidation.class;
                break;
            case "email":
                fieldName = "email";
                validationGroup = UserValidationDto.EmailValidation.class;
                break;
            case "phone_number":
                fieldName = "phone_number";
                validationGroup = UserValidationDto.PhoneValidation.class;
                break;
            case "birth":
                fieldName = "birth";
                validationGroup = UserValidationDto.BirthValidation.class;
                break;
            default:
                throw new BusinessException("INVALID_TYPE", type + "= 지원하지 않는 유효성 검사 타입입니다: ");
        }

        Set<ConstraintViolation<UserValidationDto>> violations = validator.validateValue(
                UserValidationDto.class,
                fieldName,
                value,
                validationGroup);

        if (!violations.isEmpty()) {
            ConstraintViolation<UserValidationDto> violation = violations.iterator().next();
            String errorMessage = violation.getMessage();
            log.warn("필드 유효성 검사 실패: type={}, value={}, error={}", type, value, errorMessage);
            return Map.of("success", false, "message", errorMessage);
        }
        switch (fieldName) {
            case "login_id":
                return validateLoginIdField(value);
            case "password":
                return validatePasswordField(value);
            case "name":
                return validateNameField(value);
            case "nickname":
                return validateNicknameField(value);
            case "email":
                return validateEmailField(value);
            case "phone_number":
                return validatePhoneNumberField(value);
            case "birth":
                return validateBirthField(value);
            default:
                throw new BusinessException("UNEXPECTED_ERROR", "알 수 없는 필드 타입입니다: " + fieldName);
        }
    }

    @Override
    public Map<String, Object> duplicationChecker(String type, String value) {
        log.info("duplicationChecker 호출됨: type={}, value={}", type, value);
        String typeName = "";
        String dbColumnName = type;
        String valueToQuery = value;

        switch (type) {
            case "login_id":
                typeName = "아이디";
                dbColumnName = "login_id";
                break;
            case "nickname":
                typeName = "닉네임";
                dbColumnName = "nickname";
                break;
            case "email":
                typeName = "이메일";
                dbColumnName = "email";
                break;
            case "phone_number":
                typeName = "휴대폰 번호";
                dbColumnName = "phone_number";
                try {
                    valueToQuery = aesEncoderDecoder.encode(value);
                    log.info("전화번호 암호화 완료: original={}, encoded={}", value, valueToQuery);
                } catch (Exception e) {
                    log.error("전화번호 암호화 중 오류 발생: {}", value, e);
                    return Map.of("duplicate", true, "message", "전화번호 검증 중 오류가 발생했습니다.");
                }
                break;
            default:
                log.warn("duplicationChecker에서 처리하지 않는 타입: {}", type);
                return Map.of("duplicate", false, "message", "");
        }

        int count = validationMapper.countByTypeAndValue(dbColumnName, valueToQuery);
        boolean isDuplicate = count > 0;
        String message = isDuplicate ? value + "은(는) 이미 사용 중인 " + typeName + " 입니다."
                : "사용 가능한 " + typeName + " 입니다.";

        if (typeName.equals("휴대폰 번호") && isDuplicate) {
            message = "사용할 수 없는 번호입니다.";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", isDuplicate);
        result.put("message", message);
        return result;
    }

    @Override
    public Map<String, Object> lastChecker(UserValidationDto dto) {
        Map<String, Object> errorResult = new HashMap<>();
        boolean hasError = false;

        processCheck("phone_number", dto.getPhone_number(), errorResult);
        processCheck("login_id", dto.getLogin_id(), errorResult);
        processCheck("email", dto.getEmail(), errorResult);
        processCheck("nickname", dto.getNickname(), errorResult);
        processCheck("password", dto.getPassword(), errorResult);
        processCheck("name", dto.getName(), errorResult);
        processCheck("birth", dto.getBirth(), errorResult);

        if (!errorResult.isEmpty()) {
            hasError = true;
        }

        if (hasError) {
            errorResult.put("finally", "false");
            log.warn("최종 유효성 검사 실패: {}", errorResult);
        } else {
            errorResult.put("finally", "true");
            errorResult.put("message", "모든 검증을 통과했습니다.");
            log.info("최종 유효성 검사 성공");
        }

        return errorResult;
    }

    private void processCheck(String type, String value, Map<String, Object> errorResult) {
        if (value == null || value.trim().isEmpty()) {
            if ("email".equals(type) || "birth".equals(type)) {
                log.debug("{} 필드는 값이 없어 검증을 생략합니다.", type);
                return;
            }
            log.warn("{} 필드 값이 비어있습니다. (lastChecker 진입 전 검증 필요)", type);
            errorResult.put(type, type + " 필드는 필수입니다.");
            return;
        }

        try {
            Map<String, Object> validationResult = validateSingleField(type, value);

            if (Boolean.FALSE.equals(validationResult.get("success"))) {
                errorResult.put(type, validationResult.get("message"));
            }
        } catch (BusinessException e) {
            log.error("processCheck 중 BusinessException 발생: type={}, error={}", type, e.getMessage());
            errorResult.put(type, e.getMessage());
        } catch (Exception e) {
            log.error("processCheck 중 예상치 못한 오류 발생: type={}", type, e);
            errorResult.put(type, "검증 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Override
    public boolean checkAnyDuplications(String type, String value) {
        log.warn("checkAnyDuplications 메서드는 리팩토링 필요 또는 사용되지 않을 수 있습니다.");
        try {
            Map<String, Object> result = validateSingleField(type, value);
            return !Boolean.TRUE.equals(result.get("success"));
        } catch (Exception e) {
            log.error("checkAnyDuplications 실행 중 오류", e);
            return true;
        }
    }

    private Map<String, Object> validateLoginIdField(String value) {
        log.info("validateLoginIdField 호출됨: value={}", value);
        return duplicationChecker("login_id", value);
    }

    private Map<String, Object> validateNicknameField(String value) {
        log.info("validateNicknameField 호출됨: value={}", value);
        if (Arrays.asList(badWordsLibrary.getBadWords()).contains(value)) {
            return Map.of("success", false, "message", "닉네임에 부적절한 단어가 포함되어 있습니다.");
        }
        return duplicationChecker("nickname", value);
    }

    private Map<String, Object> validateEmailField(String value) {
        log.info("validateEmailField 호출됨: value={}", value);
        return duplicationChecker("email", value);
    }

    private Map<String, Object> validatePhoneNumberField(String value) {
        log.info("validatePhoneNumberField 호출됨: value={}", value);
        return duplicationChecker("phone_number", value);
    }

    private Map<String, Object> validateBirthField(String value) {
        log.info("validateBirthField 호출됨: value={}", value);
        return validateBirthDate(value);
    }

    @Override
    public Map<String, Object> validateBirthDate(String value) {
        if (value == null || !value.matches("\\d{8}")) {
            return Map.of("success", false, "message", "생년월일은 8자리 숫자(YYYYMMDD)여야 합니다.");
        }
        Map<String, Object> ageRangeResult = isValidAgeRange(value);
        if (ageRangeResult.containsKey("false") || ageRangeResult.containsKey("NotProperDate")) {
            String message = (String) ageRangeResult.getOrDefault("NotProperDate", "만 14세 이상, 100세 이하만 가입 가능합니다.");
            message = message.replace("<br>", " ");
            return Map.of("success", false, "message", message);
        }

        return Map.of("success", true, "message", "");
    }

    @Override
    public Map<String, Object> isValidAgeRange(String birthDate) {
        try {
            LocalDate birth = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate today = LocalDate.now();
            int age = Period.between(birth, today).getYears();

            if (age >= 14 && age <= 100) {
                return Map.of("true", "");
            } else {
                return Map.of("false", "");
            }
        } catch (Exception e) {
            log.error("생년월일 파싱 또는 나이 계산 오류: {}", birthDate, e);
            return Map.of("NotProperDate", "생년월일 형식이 올바르지 않습니다.");
        }
    }

    private Map<String, Object> validatePasswordField(String value) {
        log.info("validatePasswordField 호출됨: value={}", value);
        return Map.of("success", true, "message", "사용 가능한 비밀번호입니다.");
    }

    private Map<String, Object> validateNameField(String value) {
        log.info("validateNameField 호출됨: value={}", value);
        return Map.of("success", true, "message", "");
    }
}