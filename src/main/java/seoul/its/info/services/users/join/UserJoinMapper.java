package seoul.its.info.services.users.join;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserJoinMapper {
    @Insert("INSERT INTO users (" +
            "login_id, password, name, nickname, gender, email, birth, " +
            "phone_number, phone_verified, phone_verified_at, " +
            "address_postcode, address_base, address_detail, " +
            "agreement_age, agreement_service, agreement_privacy, agreement_alba, agreement_marketing, agreement_benefits "
            +
            ") VALUES (" +
            "#{login_id}, #{password}, #{name}, #{nickname}, #{gender}, #{email}, #{birth}, " +
            "#{phone_number}, #{phone_verified}, #{phone_verified_at}, " +
            "#{address_postcode}, #{address_base}, #{address_detail}, " +
            "#{agreement_age}, #{agreement_service}, #{agreement_privacy}, #{agreement_alba}, #{agreement_marketing}, #{agreement_benefits} "
            + ")")
    void userJoinConfirm(UserJoinDto userJoinDto);
}