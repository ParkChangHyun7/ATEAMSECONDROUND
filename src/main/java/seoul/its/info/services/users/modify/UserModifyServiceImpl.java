package seoul.its.info.services.users.modify;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import seoul.its.info.common.security.PasswordEncoder;
import seoul.its.info.common.security.aesencryptor.AESEncoderDecoder;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserModifyServiceImpl implements UserModifyService {

    private final UserModifyMapper userModifyMapper;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private AESEncoderDecoder aesEncoderDecoder;

    @Override
    public Map<String, Object> getUserInfo(String loginId) {
        log.info("[getUserInfo] loginId: {}", loginId);
        Map<String, Object> result = new HashMap<>();
        UserModifyDto user = userModifyMapper.selectUserByLoginId(loginId);
        if (user != null && user.getPhone_number() != null) {
            try {
                user.setPhone_number(aesEncoderDecoder.decode(user.getPhone_number()));
            } catch (Exception e) {
                log.error("phone_number decode error", e);
            }
        }
        boolean canChangeNickname = true;
        if (user != null && user.getNickname_changed_at() != null) {
            LocalDateTime changedAt = user.getNickname_changed_at();
            long days = ChronoUnit.DAYS.between(changedAt, LocalDateTime.now());
            canChangeNickname = days >= 90;
        }
        log.info("[getUserInfo] user from DB: {}", user);
        result.put("user", user);
        result.put("can_change_nickname", canChangeNickname);
        return result;
    }

    @Override
    public boolean checkPassword(String loginId, String password) {
        String encoded = userModifyMapper.selectPasswordByLoginId(loginId);
        return passwordEncoder.matches(password, encoded);
    }

    @Override
    public Map<String, Object> modifyUserInfo(UserModifyDto dto, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (dto.getPhone_number() != null) {
            try {
                dto.setPhone_number(aesEncoderDecoder.encode(dto.getPhone_number()));
            } catch (Exception e) {
                log.error("phone_number encode error", e);
            }
        }
        UserModifyDto before = userModifyMapper.selectUserByLoginId(dto.getLogin_id());
        if (before != null && dto.getNickname() != null && !dto.getNickname().equals(before.getNickname())) {
            dto.setNickname_changed_at(LocalDateTime.now());
        }
        int updateResult = userModifyMapper.updateUserInfo(dto);
        result.put("success", updateResult > 0);
        log.info("[modifyUserInfo] dto: {}", dto);
        return result;
    }
} 