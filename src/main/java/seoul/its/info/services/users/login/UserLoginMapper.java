
package seoul.its.info.services.users.login;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map; // Map 사용을 위해 추가

@Mapper
public interface UserLoginMapper {

    @Select("SELECT id, login_id, nickname, password AS encoded_password, password_updated_at, login_fail_count, role, last_login_at FROM users WHERE login_id = #{loginId}")
    //  사용자 ID로 사용자 정보를 조회하는 쿼리입니다.
    Map<String, Object> findUserById(String loginId);

    @Update("UPDATE users SET login_fail_count = login_fail_count + 1 WHERE login_id = #{loginId}")
    //  로그인 실패 시 실패 횟수를 1 증가시킵니다
    void updateLoginFailCount(String loginId);

    @Update("UPDATE users SET login_fail_count = 0 WHERE login_id = #{loginId}")
    //  로그인 성공 시 실패 횟수를 초기화합니다
    void resetLoginFailCount(String loginId);

    @Update("UPDATE users SET last_login_at = NOW() WHERE login_id = #{loginId}")
    //  로그인 성공 시 마지막 로그인 시간을 갱신합니다
    void updateLastLoginAt(String loginId);
}