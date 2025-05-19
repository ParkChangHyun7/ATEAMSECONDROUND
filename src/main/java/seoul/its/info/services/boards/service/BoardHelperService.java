package seoul.its.info.services.boards.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface BoardHelperService {

    /**
     * 사용자가 특정 게시판을 읽을 권한이 있는지 확인합니다.
     *
     * @param boardId      게시판 ID
     * @param userDetails  현재 인증된 사용자 정보
     * @return 읽을 권한이 있으면 true, 없으면 false
     */
    boolean canReadBoard(Long boardId, UserDetails userDetails);

    /**
     * 사용자가 특정 게시판에 글을 쓸 권한이 있는지 확인합니다.
     *
     * @param boardId      게시판 ID
     * @param userDetails  현재 인증된 사용자 정보
     * @return 글을 쓸 권한이 있으면 true, 없으면 false
     */
    boolean canWriteBoard(Long boardId, UserDetails userDetails);
}
