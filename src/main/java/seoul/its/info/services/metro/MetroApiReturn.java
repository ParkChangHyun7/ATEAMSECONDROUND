package seoul.its.info.services.metro;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetroApiReturn {

	
	// api 연결된 자료 받아오는 메소드
	public Mono<Map<String, Object>> api(String searchUrl) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(searchUrl))
				.build();

		CompletableFuture<Map<String, Object>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenApply(json -> {
					try {
						ObjectMapper mapper = new ObjectMapper();
						return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});

		return Mono.fromFuture(future);
	}
	
	public Mono<String> xmlApi(String url) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.build();

		CompletableFuture<String> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body);

		return Mono.fromFuture(future);
	}
	
   
}
