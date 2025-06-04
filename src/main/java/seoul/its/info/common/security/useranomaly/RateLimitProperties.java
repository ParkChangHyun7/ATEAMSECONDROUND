package seoul.its.info.common.security.useranomaly;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate-limiting")
// application.properties에 있는 rate-limiting 프리픽스를 가진 설정을 가져옴
@Data
public class RateLimitProperties {

   private boolean enabled = false; // 기본값 true
   private LimitConfig defaults = new LimitConfig(100, 1);
   private LimitConfig authenticated = new LimitConfig(100, 1);
   private LimitConfig anonymous = new LimitConfig(50, 1);
   private Map<String, Map<String, EndpointLimit>> endpoints = new HashMap<>();

   @Data
   public static class LimitConfig {
      private int limitForPeriod;
      private int periodMinutes;

      public LimitConfig() {
      }

      public LimitConfig(int limitForPeriod, int periodMinutes) {
         this.limitForPeriod = limitForPeriod;
         this.periodMinutes = periodMinutes;
      }
   }

   @Data
   @EqualsAndHashCode(callSuper = true)
   public static class EndpointLimit extends LimitConfig {
      private boolean authenticatedOnly = false;
   }
}