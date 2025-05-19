package seoul.its.info.services.boards.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import seoul.its.info.services.boards.BoardMapper;
import seoul.its.info.services.boards.dto.BoardsRoleDto;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;

@Service
public class BoardHelperServiceImpl implements BoardHelperService {

    private final BoardMapper boardMapper;

    public BoardHelperServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    /**
     * UserDetails에서 사용자의 역할을 안전하게 추출합니다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @return 사용자의 역할 레벨 (정수), 정보가 없으면 0
     */
    private Integer getUserRoleFromUserDetails(UserDetails userDetails) {
        Integer userRole = 0;
        if (userDetails instanceof UserDetailsImpl) {
            UserDetailsImpl customUserDetails = (UserDetailsImpl) userDetails;
            if (customUserDetails.getRole() != null) {
                userRole = customUserDetails.getRole();
            }
        } else if (userDetails != null) {
            String authority = userDetails.getAuthorities().stream()
                                   .findFirst()
                                   .map(auth -> auth.getAuthority())
                                   .orElse("0");
            try {
                userRole = Integer.parseInt(authority);
            } catch (NumberFormatException e) {
                // 권한 문자열이 숫자가 아닐 경우 기본값(0) 유지 또는 로깅 (필요시 여기에 로그 추가)
            }
        }
        return userRole;
    }

    @Override
    public boolean canReadBoard(Long boardId, UserDetails userDetails) {
        BoardsRoleDto boardRoleInfo = boardMapper.getBoardRoleInfo(boardId);

        // 게시판이 존재하지 않거나 비활성화 상태면 읽기 권한 없음
        if (boardRoleInfo == null || boardRoleInfo.getIsActive() == 0) {
            return false;
        }

        Integer userRole = getUserRoleFromUserDetails(userDetails);

        // 사용자의 역할이 게시판의 readRole 이상인지 확인
        return userRole >= boardRoleInfo.getReadRole();
    }

    @Override
    public boolean canWriteBoard(Long boardId, UserDetails userDetails) {
         BoardsRoleDto boardRoleInfo = boardMapper.getBoardRoleInfo(boardId);

        // 게시판이 존재하지 않거나 비활성화 상태면 쓰기 권한 없음
        if (boardRoleInfo == null || boardRoleInfo.getIsActive() == 0) {
            return false;
        }

        Integer userRole = getUserRoleFromUserDetails(userDetails);

        // 사용자의 역할이 게시판의 writeRole 이상인지 확인
        return userRole >= boardRoleInfo.getWriteRole();
    }
}
