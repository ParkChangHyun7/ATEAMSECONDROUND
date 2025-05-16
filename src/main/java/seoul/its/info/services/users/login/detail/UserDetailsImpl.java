package seoul.its.info.services.users.login.detail;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String loginId;
    private String password;
    private String nickname;
    private Integer role;

    public UserDetailsImpl(Long id, String loginId, String password, String nickname, Integer role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority;
        if (this.role == null) {
            authority = "ROLE_USER";
        } else if (this.role == 100) {
            authority = "ROLE_ADMIN";
        } else {
            authority = "ROLE_USER";
        }
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getRole() {
        return role;
    }
}