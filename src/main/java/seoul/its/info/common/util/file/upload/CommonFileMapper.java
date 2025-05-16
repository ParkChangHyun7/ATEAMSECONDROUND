package seoul.its.info.common.util.file.upload;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import seoul.its.info.common.util.file.upload.dto.FileUploadDto;

@Mapper
public interface CommonFileMapper {

    @Insert("INSERT INTO files (post_id, comment_id, contact_id, service_id, upload_from, user_id, login_id, " +
            "original_name, saved_name, file_path, file_size, file_type, risk_level, " +
            "uploader_ip_address, is_deleted, upload_by_anonymous) " +
            "VALUES (#{postId}, #{commentId}, #{contactId}, #{serviceId}, #{uploadFrom}, #{userId}, #{loginId}, " +
            "#{originalName}, #{savedName}, #{filePath}, #{fileSize}, #{fileType}, #{risklevel}, " +
            "#{uploaderIpAddress}, #{isDeleted}, #{uploadByAnonymous})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFile(FileUploadDto fileDto);
}