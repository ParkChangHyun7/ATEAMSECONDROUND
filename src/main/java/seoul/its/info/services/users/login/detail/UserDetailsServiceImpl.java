package seoul.its.info.services.users.login.detail;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import seoul.its.info.services.users.login.UserLoginMapper;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserLoginMapper userLoginMapper;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Map<String, Object> user = userLoginMapper.findUserById(loginId);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with loginId: " + loginId);
        }

        Integer role = null;
        Object roleObj = user.get("role");
        if (roleObj instanceof Number) {
            role = ((Number) roleObj).intValue();
        }

        return new UserDetailsImpl(
                ((Number) user.get("id")).longValue(),
                (String) user.get("login_id"),
                (String) user.get("encoded_password"),
                (String) user.get("nickname"),
                role
        );
    }
} 