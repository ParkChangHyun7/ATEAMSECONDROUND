package seoul.its.info.services.users.validation.banned;

import lombok.Data;

@Data
public class BannedUserDto {
    private String user_id;
    private String nickname;
    private String email;
    private String phone_number;
    private String user_ip;
    private String user_agent;
    private String phone_ci_provider;
    private String phone_ci;
    private String phone_di;
    private String device_fingerprint;
    private String user_from;
    private String social_provider;
    private String social_user_id;
    private String banned_reason;
    private String banned_date;
}