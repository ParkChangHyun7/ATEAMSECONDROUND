package seoul.its.info.services.users.login.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import seoul.its.info.common.exception.SystemException;
import seoul.its.info.services.users.login.dto.UserLoginRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {

    @Override
    public String loginGet(Model model, HttpServletRequest request) throws SystemException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            model.addAttribute("pageTitle", "로그인");
            model.addAttribute("contentPage", "../content_pages/users/login/login.jsp");
            model.addAttribute("scriptsPage", "../include/users/login/scripts.jsp");
            model.addAttribute("resourcesPage", "../include/users/login/resources.jsp");
            return "templates/base";
        } else {
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
            redirectAttributes.addFlashAttribute("message", "이미 로그인되어 있습니다.");
            String contextPath = request.getContextPath();
            if (contextPath == null || contextPath.isEmpty()) {
                contextPath = "/";
            }
            return "redirect:" + contextPath;
        }
    }
    
    @Override
    public boolean validateUser(UserLoginRequestDto loginDto) {
    	
    	return loginDto != null&& "test".equals(loginDto.getLoginId())&&"1234".equals(loginDto.getPassword());
    }
}