package seoul.its.info.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.http.HttpStatus;

import seoul.its.info.common.security.useranomaly.RateLimitingInterceptor;

// Value 임포트
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CommonConfig implements WebMvcConfigurer {

        private final TimeoutInterceptor timeoutInterceptor;
        private final RateLimitingInterceptor rateLimitingInterceptor;

        @Value("${app.image.upload-dir}")
        private String uploadDir;

        public CommonConfig(TimeoutInterceptor timeoutInterceptor, RateLimitingInterceptor rateLimitingInterceptor) {
                this.timeoutInterceptor = timeoutInterceptor;
                this.rateLimitingInterceptor = rateLimitingInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                System.out.println("CommonConfig: 인터셉터 등록 중...");

                registry.addInterceptor(timeoutInterceptor)
                                .addPathPatterns("/**")
                                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/fonts/**", "/api/**",
                                                "/vue.js/**", "*/favicon.ico");

                registry.addInterceptor(rateLimitingInterceptor)
                                .addPathPatterns("/**")
                                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/fonts/**", "/vue.js/**",
                                                "*/favicon.ico");
        }

        @Override
        public void addViewControllers(@NonNull ViewControllerRegistry registry) {
            registry.addViewController("/.well-known/appspecific/com.chrome.devtools.json").setStatusCode(HttpStatus.OK);
        }

        @Override
        public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                // css, js, images, fonts 리소스 폴더 설정
                registry.addResourceHandler("/css/**")
                                .addResourceLocations("classpath:/static/css/")
                                .setCachePeriod(30); // 개발 환경: 30초 캐시
                // .setCachePeriod(60 * 60 * 24 * 365); // 운영 환경: 1년 캐시

                // favicon.ico 특정 핸들러 추가
                registry.addResourceHandler("/favicon.ico")
                                .addResourceLocations("classpath:/static/images/");

                registry.addResourceHandler("/js/**")
                                .addResourceLocations("classpath:/static/js/")
                                .setCachePeriod(30); // 개발 환경: 30초 캐시
                // .setCachePeriod(60 * 60 * 24 * 365); // 운영 환경: 1년 캐시

                // Vue.js 파일 리소스 설정 추가
                registry.addResourceHandler("/vue.js/**")
                                .addResourceLocations("classpath:/static/vue.js/")
                                .setCachePeriod(30); // 개발 환경: 30초 캐시
                // .setCachePeriod(60 * 60 * 24 * 365); // 운영 환경: 1년 캐시

                registry.addResourceHandler("/images/**")
                                .addResourceLocations("classpath:/static/images/")
                                .setCachePeriod(60 * 60 * 24 * 365);

                registry.addResourceHandler("/fonts/**")
                                .addResourceLocations("classpath:/static/fonts/")
                                .setCachePeriod(30); // 개발 환경: 30초 캐시
                // .setCachePeriod(60 * 60 * 24 * 365); // 운영 환경: 1년 캐시

                // JSON 파일 리소스 설정
                registry.addResourceHandler("/com/json/**")
                                .addResourceLocations("classpath:/com/json/")
                                .setCachePeriod(60 * 60 * 24 * 365);
                // 원래 60분 캐시였는데 지금 사용자가 캐싱해둘 json 파일이 없음
                // 그나마 있다면 도메인 확인용 tld였나 그럴건데 그나마도
                // 인메모리 방식으로 변경해서 캐시시간 1년으로 조정함.
                registry.addResourceHandler("/videos/**")
                                .addResourceLocations("classpath:/static/videos/")
                                .setCachePeriod(60 * 60);

                // 업로드 이미지 파일 리소스 핸들러 설정
                String resolvedUploadDir = uploadDir.replace("\\", "/"); // Windows 경로 구분자를 '/'로 통일
                if (!resolvedUploadDir.endsWith("/")) {
                    resolvedUploadDir += "/";
                }
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + resolvedUploadDir) // 'file:' 접두사 사용
                        .setCachePeriod(30); // 개발 중에는 짧게 (예: 30초 또는 0)
        }
}