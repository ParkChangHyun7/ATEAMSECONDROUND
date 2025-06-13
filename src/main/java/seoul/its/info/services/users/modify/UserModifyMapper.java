package seoul.its.info.services.users.modify;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

@Mapper
public interface UserModifyMapper {
    @Select("SELECT login_id, name, nickname, email, phone_number, birth, address_postcode, address_base, address_detail, gender, nickname_changed_at, agreement_age, agreement_service, agreement_privacy, agreement_alba, agreement_marketing, agreement_benefits FROM users WHERE login_id = #{loginId}")
    @Results({
        @Result(property = "login_id", column = "login_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "nickname", column = "nickname"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone_number", column = "phone_number"),
        @Result(property = "birth", column = "birth"),
        @Result(property = "address_postcode", column = "address_postcode"),
        @Result(property = "address_base", column = "address_base"),
        @Result(property = "address_detail", column = "address_detail"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "nickname_changed_at", column = "nickname_changed_at"),
        @Result(property = "agreement_age", column = "agreement_age", javaType = Boolean.class),
        @Result(property = "agreement_service", column = "agreement_service", javaType = Boolean.class),
        @Result(property = "agreement_privacy", column = "agreement_privacy", javaType = Boolean.class),
        @Result(property = "agreement_alba", column = "agreement_alba", javaType = Boolean.class),
        @Result(property = "agreement_marketing", column = "agreement_marketing", javaType = Boolean.class),
        @Result(property = "agreement_benefits", column = "agreement_benefits", javaType = Boolean.class)
    })
    UserModifyDto selectUserByLoginId(@Param("loginId") String loginId);

    @Select("SELECT password FROM users WHERE login_id = #{loginId}")
    String selectPasswordByLoginId(@Param("loginId") String loginId);

    @Select("SELECT phone_number FROM users WHERE login_id = #{loginId}")
    String selectPhoneNumberByLoginId(@Param("loginId") String loginId);

    @Update({
        "<script>",
        "UPDATE users",
        "<set>",
        "  <if test='new_password != null and new_password != \"\"'>password = #{new_password},</if>",
        "  <if test='name != null'>name = #{name},</if>",
        "  <if test='nickname != null'>nickname = #{nickname}, nickname_changed_at = #{nickname_changed_at},</if>",
        "  <if test='email != null'>email = #{email},</if>",
        "  <if test='birth != null'>birth = #{birth},</if>",
        "  <if test='gender != null'>gender = #{gender},</if>",
        "  <if test='address_postcode != null'>address_postcode = #{address_postcode},</if>",
        "  <if test='address_base != null'>address_base = #{address_base},</if>",
        "  <if test='address_detail != null'>address_detail = #{address_detail},</if>",
        "  <if test='phone_number != null'>phone_number = #{phone_number},</if>",
        "  <if test='agreement_age != null'>agreement_age = #{agreement_age},</if>",
        "  <if test='agreement_service != null'>agreement_service = #{agreement_service},</if>",
        "  <if test='agreement_privacy != null'>agreement_privacy = #{agreement_privacy},</if>",
        "  <if test='agreement_alba != null'>agreement_alba = #{agreement_alba},</if>",
        "  <if test='agreement_marketing != null'>agreement_marketing = #{agreement_marketing},</if>",
        "  <if test='agreement_benefits != null'>agreement_benefits = #{agreement_benefits},</if>",
        "</set>",
        "WHERE login_id = #{login_id}",
        "</script>"
    })
    int updateUserInfo(UserModifyDto dto);
} 