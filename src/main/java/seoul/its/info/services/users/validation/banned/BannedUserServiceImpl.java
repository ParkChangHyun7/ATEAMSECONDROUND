package seoul.its.info.services.users.validation.banned;

// import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// @RequiredArgsConstructor
@Slf4j
public class BannedUserServiceImpl implements BannedUserService {

   // private final BannedUserMapper bannedUserMapper;

   @Autowired
   private BannedUserMapper bannedUserMapper;

   @Override
   public boolean isUserBanned(BannedUserDto dto) {
      log.debug("Checking if user is banned based on DTO: {}", dto);

      // 실제 적용 시 검증할 로직 구상도..
      // 개인정보 약관에 동의한 정보들만 수집하여 검증을 진행해야함. *필수
      // 근데 Dto 담긴 정보들 대부분 동의 받을 거라서..

      // user_ip, user_agent, social_provider 등 유저 특정 기준 절대값이
      // 될 수 없는 값들은 보조 수단으로만 사용함.
      // 주요 식별자(email, phone_number, ci, di)로 1차 조회 후,
      // 결과가 없을 때 보조 식별자(ip, agent 등)로 의심 패턴 매칭 또는
      // 로깅/모니터링 강화 등에 활용 가능.
      // 보조 수단 정도만 매칭될 경우는 코드단에서 통제가 아닌 관리자가
      // 직접 확인하도록 설계해야 될 것 같은데... 관리자 기능은
      // 또 언제 만드냐..

      // Mapper를 호출하여 DB에서 실제 금지 여부 확인
      boolean isBanned = bannedUserMapper.checkIfBanned(dto);

      if (isBanned) {
         log.warn("차단된 사용자의 활동 기록: {}", dto);
      } else {
         log.debug("User is not banned.");
      }

      // 실제 구현에서는 isBanned 값을 반환해야 함.
      return false; // 실제는 검증 결과값을 반환해야됨
      // 지금은 필요성 인지 + 골격만 갖추기 위해서 항상 false.
   }
}