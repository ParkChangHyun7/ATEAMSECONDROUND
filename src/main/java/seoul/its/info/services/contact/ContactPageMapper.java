package seoul.its.info.services.contact;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ContactPageMapper {

    @Insert("INSERT INTO contacts (name, email, phone_num, contact_subject, contact_message, file_included) " +
            "VALUES (#{name}, #{email}, #{phone}, #{subject}, #{message}, #{fileIncluded})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertContactInquiry(ContactRequestDto contactDto);

    @Insert("INSERT INTO contacts (name, email, phone_num, contact_subject, contact_message, file_included) " +
            "VALUES (#{name}, #{email}, #{phone}, #{subject}, #{message}, #{fileIncluded})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertContactInquiryWithAttachment(ContactRequestDto contactDto);

}