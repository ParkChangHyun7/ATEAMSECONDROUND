// UserJoinDto.java - 회원가입 시 클라이언트로부터 받는 데이터 정의 + 유효성 검사
package seoul.its.info.services.users.join;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import seoul.its.info.services.users.validation.UserValidationDto;

@Getter
@Setter
public class UserJoinDto extends UserValidationDto {
    // UserValidationDto 상속함. 이유는!
    // 회원가입 중간 단계에서 정규식 검사와 중복 검사를 하는 과정은
    // validation 모듈을 통해서 처리하고 있었는데 결국 회원가입 과정의
    // 정규식 검사와 중복되는 문제가 있었음. 그래서 이왕 회원가입 과정 전반에 걸쳐
    // 사용될 값이라면 UserValidation에 정의 및 정규식 검사를 하도록 하고
    // UserJoin에서 해당 값을 상속 받도록 해버리면 2중 정의에 대한 문제가 사라짐
    // 컴포지션 방식도 고려하였으나 두개의 Dto가 코드 안에서 반복적으로
    // 나오게 되면서 코드 가독성이 지나치게 떨어지길래 상속으로 변경함.
    // 상속으로 인한 문제인 validation쪽의 dto 수정이 요구될 경우
    // validation쪽에 신규 dto를 신설하는 쪽으로 방향을 잡았음.

    private String gender;

    private String address_postcode;

    private String address_base;

    private String address_detail;

    private Boolean phone_verified;

    private LocalDateTime phone_verified_at;

    private boolean agreement_age;

    private boolean agreement_service;

    private boolean agreement_privacy;

    private boolean agreement_alba;

    private boolean agreement_marketing;

    private boolean agreement_benefits;

    // 부모로부터 상속받은 필드(user_id, nickname, email, phone_number)는 여기에 정의하지 않음
}