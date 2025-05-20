package seoul.its.info.services.metro;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MetroApiReturn {

	private final WebClient webClientForJson;
	private final WebClient webClientForXml;

	
	// api 연결된 자료 받아오는 메소드
	public Mono<Map> api(String searchUrl) {
		// WebClient를 사용해 요청 보내기
		Mono<Map> response = webClientForJson.get()
				.uri(searchUrl) // API URL 설정
				.retrieve()
				.bodyToMono(Map.class); // JSON 응답을 String으로 받음

		// 결과 출력
		System.out.println("API 응답 데이터: " + response);
		return response;

	}
	
	public Mono<String> xmlApi(String url) {
	    return webClientForXml.get()
	            .uri(url)
	            .retrieve()
	            .bodyToMono(String.class);  
	}
	
   
}
