package seoul.its.info.services.metro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientConfig클래스
 * 이 클래스는 WebClient 인스턴스를 Bean으로 등록해주는 설정 클래스입니다.
 * WebClient는 외부 API 호출에 사용하는 비동기 HTTP클라이언트입니다.
 * 이 Bean은 MetroApiReturn 클래스에서 @Autowired(생성자 주입)으로 사용됩니다.
 */

 @Configuration //이 클래스가 스프링 설정 파일이라는 걸 알려주는 어노테이션
public class WebClientConfig {

    /**
     * JSON 응답을 처리할 WebClient Bean 등록
     * -메서드 이름이 Bean의 이름이 됨 -> webClientJson
     * -MetroApiReturn.java에서 이 이름으로 주입받고 있음
     */

    @Bean
    public WebClient webClientForJson(){
        return WebClient.builder().build(); //기본 설정 WebClient 생성
    }
    /**
     * XML 응답을 처리할 WebClient Bean 등록
     *-메서드 이름이 Bean 이름이 됨 -> webClientForXml
     *-MetroApiReturn.java에서 이 이름으로 주입받고 있음
     */
    
     @Bean
     public WebClient webClientForXml(){
            return WebClient.builder().build(); //XML API도 같은 방식으로 호출 가능
        }
     }
