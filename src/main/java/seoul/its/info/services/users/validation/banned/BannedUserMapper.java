package seoul.its.info.services.users.validation.banned;

// import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

// @Mapper
@Service //일단 시험 작동용으로 서비스 빈으로 등록함 실제 구현 시에는 맵퍼가 돼야됨.
public class BannedUserMapper {
   // 고로 맵퍼는 인터페이스로 구현해야하고 추상 메서드에 결과값 명시해버리면 컴파일 에러나니 주의.
   boolean checkIfBanned(BannedUserDto dto) {
      return false;
   };
   // 실제 쿼리 구현 시:
   // 전달된 dto의 필드들(email, phone_number, phone_ci, phone_di, user_ip, user_agent 등등)
   // 조회해서 banned_users 테이블 또는 관련 테이블에서 일치하는 기록이 있는지 확인하는 쿼리 전송해야됨.
   // 여러 조건 조합해서 조회를 여러번 하도록 해야될 수도 있는데 그건 나중에 고민해봐야 될 듯
}