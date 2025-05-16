package seoul.its.info.services.users.validation;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserValidationMapper {
    @Select("SELECT COUNT(*) FROM users WHERE login_id = #{userId}")
    int countById(String Id);

    @Select("SELECT COUNT(*) FROM users WHERE nickname = #{nickname}")
    int countByNickname(String nickname);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);

    @Select("SELECT COUNT(*) FROM users WHERE ${type} = #{value}")
    int countByTypeAndValue(String type, String value);
}