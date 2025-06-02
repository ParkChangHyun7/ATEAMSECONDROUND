package seoul.its.info.common.util.file.imageupload;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import seoul.its.info.common.util.file.imageupload.dto.ImageDto;

import java.util.List;

@Mapper
public interface ImageMapper {

    /**
     * 파일 메타데이터를 files 테이블에 저장합니다.
     *
     * @param fileDto 저장할 파일 메타데이터
     * @return 저장된 행 수
     */
    @Insert("INSERT INTO files (post_id, comment_id, contact_id, service_id, upload_from, user_id, login_id, original_name, saved_name, is_image, file_path, file_size, file_type, risk_level, uploader_ip_address, upload_by_anonymous, created_at) " +
            "VALUES (#{file.post_id}, #{file.comment_id}, #{file.contact_id}, #{file.service_id}, #{file.upload_from}, #{file.user_id}, #{file.login_id}, #{file.original_name}, #{file.saved_name}, #{file.is_image}, #{file.file_path}, #{file.file_size}, #{file.file_type}, #{file.risk_level}, #{file.uploader_ip_address}, #{file.upload_by_anonymous}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "file.id") // 생성된 키 값을 fileDto 객체의 id에 설정
    int saveFileMetadata(@Param("file") ImageDto fileDto);

    /**
     * 임시 parentId로 저장된 이미지 파일들의 parentId를 최종 parentId로 업데이트합니다.
     *
     * @param temporaryParentId 임시 parentId
     * @param finalParentId 최종 parentId
     * @return 업데이트된 행 수
     */
    @Update("UPDATE files SET post_id = #{finalParentId} " +
            "WHERE post_id = #{temporaryParentId} AND upload_from = 0") // upload_from = 0은 게시글을 의미한다고 가정
    int updateFileParentId(@Param("temporaryParentId") Long temporaryParentId, @Param("finalParentId") Long finalParentId);

    /**
     * 특정 사용자가 특정 소스(게시판 등)에 대해 임시로 업로드한 파일들(post_id가 NULL인 파일들)을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param uploadFrom 파일이 업로드된 소스 (0=게시글 등)
     * @return 임시 파일 목록
     */
    @Select("SELECT * FROM files WHERE user_id = #{userId} AND upload_from = #{uploadFrom} AND post_id IS NULL")
    List<ImageDto> findTemporaryFiles(@Param("userId") Long userId, @Param("uploadFrom") int uploadFrom);

    /**
     * 특정 파일의 post_id와 file_path를 업데이트합니다.
     *
     * @param fileId 대상 파일의 ID
     * @param finalPostId 할당할 최종 게시글 ID
     * @param newFilePath 새로운 파일 경로
     * @return 업데이트된 행 수
     */
    @Update("UPDATE files SET post_id = #{finalPostId}, file_path = #{newFilePath} WHERE id = #{fileId}")
    int updatePostIdAndPathForFile(@Param("fileId") Long fileId, @Param("finalPostId") Long finalPostId, @Param("newFilePath") String newFilePath);

    // TODO: 파일 메타데이터 조회, 삭제 등의 메서드 필요에 따라 추가
} 