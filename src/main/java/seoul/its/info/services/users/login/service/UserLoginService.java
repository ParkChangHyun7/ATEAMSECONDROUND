package seoul.its.info.services.users.login.service;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import seoul.its.info.services.users.login.dto.UserLoginRequestDto;

public interface UserLoginService {
    String loginGet(Model model, HttpServletRequest request);
    
    boolean validateUser(UserLoginRequestDto loginDto);
}