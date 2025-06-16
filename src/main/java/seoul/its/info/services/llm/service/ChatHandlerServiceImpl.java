package seoul.its.info.services.llm.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import seoul.its.info.services.llm.dto.ChatHandlerRequestDto;
import seoul.its.info.services.llm.dto.ChatHandlerResponseDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ChatHandlerServiceImpl implements ChatHandlerService {

   private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
   private static final String MODEL_NAME = "gemma3:4b";

   // 데이터 분석용 (정확성, 간결함 위주)
   private static final double ANALYZE_TEMPERATURE = 0.1;
   private static final int ANALYZE_TOP_K = 30;
   private static final double ANALYZE_TOP_P = 0.9;
   private static final int ANALYZE_NUM_PREDICT = 1024; // 답변 최대 길이 (토큰)
   private static final double ANALYZE_REPEAT_PENALTY = 1.1; // 반복 패널티
   
   // 일반 대화용 (창의성, 풍부함 위주)
   private static final double GENERIC_TEMPERATURE = 0.5;
   private static final int GENERIC_TOP_K = 50;
   private static final double GENERIC_TOP_P = 0.95;
   private static final int GENERIC_NUM_PREDICT = 1024;
   private static final double GENERIC_REPEAT_PENALTY = 1.1;

   private final RestTemplate restTemplate;
   private final ObjectMapper objectMapper;
   private final WebClient webClient;

   public ChatHandlerServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
      this.restTemplate = restTemplate;
      this.objectMapper = objectMapper;
      this.webClient = WebClient.builder().baseUrl("http://localhost:11434").build();
   }

   @Override
   public String callGemmaModel(String systemPrompt, String referenceData, String userQuestion) {
      RestTemplate restTemplate = new RestTemplate();
      ObjectMapper objectMapper = new ObjectMapper();

      try {
         String fullPrompt = buildFullPrompt(systemPrompt, referenceData, userQuestion);

         ObjectNode requestPayload = objectMapper.createObjectNode();
         requestPayload.put("model", MODEL_NAME);
         requestPayload.put("prompt", fullPrompt);
         requestPayload.put("stream", false);

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestPayload), headers);

         String ollamaResponse = restTemplate.postForObject(OLLAMA_API_URL, requestEntity, String.class);

         ObjectNode responseJson = (ObjectNode) objectMapper.readTree(ollamaResponse);
         String actualResponse = responseJson.get("response").asText();

         System.out.println("Gemma 응답: " + actualResponse);
         return actualResponse;

      } catch (Exception e) {
         System.err.println("Ollama API 호출 중 오류 발생: " + e.getMessage());
         throw new RuntimeException("Gemma 모델 호출 실패", e);
      }
   }

   @Override
   public ChatHandlerResponseDto.ChatResponse handleChatRequest(ChatHandlerRequestDto.ChatRequest request) {
      System.out.println("채팅 요청 수신: " + request.getUserQuestion());

      try {
         String response = callGemmaModel(request.getSystemPrompt(), request.getReferenceData(),
               request.getUserQuestion());
         return ChatHandlerResponseDto.ChatResponse.success(response);
      } catch (Exception e) {
         System.err.println("Gemma 모델 호출 중 오류 발생: " + e.getMessage());
         return ChatHandlerResponseDto.ChatResponse.error("LLM 처리 중 오류가 발생했습니다: " + e.getMessage());
      }
   }

   @Override
   public ChatHandlerResponseDto.ChatResponse handleDataAnalyzeRequest(
         ChatHandlerRequestDto.DataAnalyzeRequest request) {
      System.out.println("데이터 분석 요청 수신: " + request.getUserQuestion());

      String dataAnalyzePrompt = buildDataAnalyzePrompt();

      try {
         String response = callGemmaModelWithTemperature(dataAnalyzePrompt, request.getReferenceData(),
               request.getUserQuestion(), 0.2);
         return ChatHandlerResponseDto.ChatResponse.success(response);
      } catch (Exception e) {
         System.err.println("데이터 분석 중 오류 발생: " + e.getMessage());
         return ChatHandlerResponseDto.ChatResponse.error("데이터 분석 중 오류가 발생했습니다: " + e.getMessage());
      }
   }

   @Override
   public SseEmitter handleDataAnalyzeStream(String userQuestion) {
      SseEmitter emitter = new SseEmitter(300_000L); // 타임아웃 5분으로 설정
      ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
      
      emitter.onCompletion(() -> {
         sseMvcExecutor.shutdown();
      });

      emitter.onTimeout(() -> {
         emitter.complete();
         sseMvcExecutor.shutdown();
      });

      sseMvcExecutor.execute(() -> {
         try {
            String referenceData = loadJsonFromClasspath("com/json/trafficAccident.json");
            String systemPrompt = buildDataAnalyzePrompt();
            
            String requestBody = buildGemmaRequestBody(systemPrompt, referenceData, userQuestion, true, 
                ANALYZE_TEMPERATURE, ANALYZE_TOP_K, ANALYZE_TOP_P, ANALYZE_NUM_PREDICT, ANALYZE_REPEAT_PENALTY);

            webClient.post()
               .uri("/api/generate")
               .accept(MediaType.APPLICATION_NDJSON)
               .contentType(MediaType.APPLICATION_JSON)
               .bodyValue(requestBody)
               .retrieve()
               .bodyToFlux(String.class)
               .doOnNext(line -> {
                  try {
                     if (line.trim().isEmpty() || !line.startsWith("{")) return;
                     // 받은 JSON 라인을 파싱하지 않고 그대로 클라이언트에 전송
                     emitter.send(SseEmitter.event().data(line));
                  } catch (IOException e) {
                     // 클라이언트 연결이 끊겼을 가능성이 높으므로, 에러를 던져 스트림 종료
                     emitter.completeWithError(e);
                  }
               })
               .doOnError(emitter::completeWithError)
               .doOnComplete(() -> {
                  // 스트림이 성공적으로 완료되면 emitter를 정상적으로 종료
                  emitter.complete();
               })
               .subscribe();

         } catch (Exception e) {
            emitter.completeWithError(e);
         }
      });
      
      return emitter;
   }

   private String loadJsonFromClasspath(String path) throws IOException {
      ClassPathResource resource = new ClassPathResource(path);
      try (InputStream inputStream = resource.getInputStream()) {
         return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }
   }

   private String buildGemmaRequestBody(String systemPrompt, String referenceData, String userQuestion, boolean stream, 
                                       double temperature, int topK, double topP, int numPredict, double repeatPenalty) {
      try {
         ObjectNode rootNode = objectMapper.createObjectNode();
         rootNode.put("model", MODEL_NAME);

         // 프롬프트 구조 변경: [참고 자료] -> [지시문] -> [질문]
         String fullPrompt = String.format(
             "아래의 [참고 자료]와 [지시문]을 바탕으로 [질문]에 답변하세요.\n\n" +
             "--- [참고 자료] ---\n%s\n\n" +
             "--- [지시문] ---\n%s\n\n" +
             "--- [질문] ---\n%s",
             referenceData, systemPrompt, userQuestion);
         
         rootNode.put("prompt", fullPrompt);
         
         ObjectNode optionsNode = objectMapper.createObjectNode();
         optionsNode.put("temperature", temperature);
         optionsNode.put("top_k", topK);
         optionsNode.put("top_p", topP);
         optionsNode.put("num_predict", numPredict);
         optionsNode.put("repeat_penalty", repeatPenalty);
         rootNode.set("options", optionsNode);
         
         rootNode.put("stream", stream);

         return objectMapper.writeValueAsString(rootNode);
      } catch (JsonProcessingException e) {
         throw new RuntimeException("JSON 요청 본문 생성 중 오류 발생", e);
      }
   }

   private String buildDataAnalyzePrompt() {
      return "[역할] 당신은 '데이터 분석 전문 AI'입니다.\n\n" +
             "[규칙]\n" +
             "1. **답변 범위**: 오직 주어진 [참고 자료]의 내용만으로 답변해야 합니다. 외부 지식을 활용하거나 데이터를 추측해서는 안됩니다.\n" +
             "2. **주제 이탈**: 사용자의 질문이 [참고 자료]와 전혀 관련 없는 경우(예: 요리법, 날씨), \"저는 데이터 분석 전문 AI이므로, 해당 질문에는 답변할 수 없습니다.\" 라고만 답변해야 합니다.\n" +
             "3. **데이터 부재**: 자료에 없는 내용에 대한 질문은 \"제공된 데이터에 해당 정보가 없습니다.\" 라고 답변하세요.\n" +
             "4. **답변 스타일**: 원본 데이터(JSON, XML 등)를 직접 노출하지 말고, 내용을 요약하고 분석하여 한국어로 설명해주세요.";
   }

   private String buildFullPrompt(String systemPrompt, String referenceData, String userQuestion) {
      StringBuilder promptBuilder = new StringBuilder();

      if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
         promptBuilder.append("SystemPrompt:\n")
               .append(systemPrompt)
               .append("\n\n");
      }

      if (referenceData != null && !referenceData.trim().isEmpty()) {
         promptBuilder.append("ReferenceData:\n")
               .append(referenceData)
               .append("\n\n");
      }

      promptBuilder.append("UserQuestion:\n")
            .append(userQuestion);

      return promptBuilder.toString();
   }

   public String callGemmaModelWithTemperature(String systemPrompt, String referenceData, String userQuestion,
         double temperature) {
      RestTemplate restTemplate = new RestTemplate();
      ObjectMapper objectMapper = new ObjectMapper();

      try {
         String fullPrompt = buildFullPrompt(systemPrompt, referenceData, userQuestion);

         ObjectNode requestPayload = objectMapper.createObjectNode();
         requestPayload.put("model", MODEL_NAME);
         requestPayload.put("prompt", fullPrompt);
         requestPayload.put("stream", false);

         // temperature 설정 추가
         ObjectNode options = objectMapper.createObjectNode();
         options.put("temperature", temperature);
         requestPayload.set("options", options);

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestPayload), headers);

         String ollamaResponse = restTemplate.postForObject(OLLAMA_API_URL, requestEntity, String.class);

         ObjectNode responseJson = (ObjectNode) objectMapper.readTree(ollamaResponse);
         String actualResponse = responseJson.get("response").asText();

         System.out.println("Gemma 응답 (temperature=" + temperature + "): " + actualResponse);
         return actualResponse;

      } catch (Exception e) {
         System.err.println("Ollama API 호출 중 오류 발생: " + e.getMessage());
         throw new RuntimeException("Gemma 모델 호출 실패", e);
      }
   }

   @Override
   public ChatHandlerResponseDto.ChatResponse handleGenericChat(String systemPrompt, String referenceData, String userQuestion) {
      String requestBody = buildGemmaRequestBody(systemPrompt, referenceData, userQuestion, false, 
          GENERIC_TEMPERATURE, GENERIC_TOP_K, GENERIC_TOP_P, GENERIC_NUM_PREDICT, GENERIC_REPEAT_PENALTY);
      String ollamaResponse = callOllamaApi(requestBody);
      return parseOllamaResponse(ollamaResponse);
   }

   private String callOllamaApi(String requestBody) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      try {
         ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API_URL, entity, String.class);
         return response.getBody();
      } catch (HttpClientErrorException e) {
         // e.g., log error and return a user-friendly message
         return "{\"error\":\"" + e.getStatusCode() + "\", \"message\":\"" + e.getResponseBodyAsString() + "\"}";
      }
   }
   
   private ChatHandlerResponseDto.ChatResponse parseOllamaResponse(String responseBody) {
      try {
         JsonNode root = objectMapper.readTree(responseBody);
         String content = root.path("response").asText();
         return ChatHandlerResponseDto.ChatResponse.success(content);
      } catch (JsonProcessingException e) {
         // Handle parsing error
         return ChatHandlerResponseDto.ChatResponse.error("LLM 응답을 파싱하는 중 에러가 발생했습니다.");
      }
   }
}
