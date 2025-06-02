package seoul.its.info.common.util.file.imageupload;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    /**
     * 이미지 파일을 업로드하고 메타데이터를 저장합니다.
     *
     * @param file 업로드된 이미지 파일
     * @param uploadFrom 파일이 업로드된 소스 (0=포스트, 1=코멘트 등)
     * @param parentId 연관된 엔티티 ID (post_id, comment_id 등)
     * @param userId 업로드한 사용자 ID
     * @param loginId 업로드한 사용자 로그인 ID
     * @param uploaderIpAddress 업로드한 사용자의 IP 주소
     * @return 업로드된 이미지에 접근할 수 있는 URL 경로
     */
    String uploadImage(MultipartFile file, int uploadFrom, Long parentId, Long userId, String loginId, String uploaderIpAddress);

    /**
     * 임시 parentId로 저장된 이미지 파일들의 parentId를 최종 parentId로 업데이트합니다.
     *
     * @param temporaryParentId 임시 parentId (임시 게시글 ID)
     * @param finalParentId 최종 parentId (최종 게시글 ID)
     */
    void updateImageParentId(Long temporaryParentId, Long finalParentId);

    /**
     * 특정 사용자가 특정 소스(게시판 등)에 대해 임시로 업로드한 파일들(post_id가 NULL인 파일들)의
     * post_id를 최종 게시글 ID로 할당하고, 필요시 파일 경로도 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param uploadFrom 파일이 업로드된 소스 (0=포스트 등)
     * @param boardId 게시판 ID
     * @param finalPostId 최종적으로 할당될 게시글 ID
     */
    void assignPostIdToTemporaryImages(Long userId, int uploadFrom, Long boardId, Long finalPostId);

    // TODO: 파일 삭제, 파일 조회 등의 메서드 필요에 따라 추가
} 