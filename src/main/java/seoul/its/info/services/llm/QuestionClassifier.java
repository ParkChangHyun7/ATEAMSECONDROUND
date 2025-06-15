package seoul.its.info.services.llm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class QuestionClassifier {

   @Autowired
   private ChatController chatController;

   @GetMapping("/chat")
   public String chatPage() {
      return "content_pages/llm/chat";
   }

   @PostMapping("/api/classifier")
   @ResponseBody
   public String classifyQuestion(@RequestBody String userQuestion) {
      System.out.println("사용자 질문: " + userQuestion);

      String ollamaApiUrl = "http://localhost:11434/api/generate";
      RestTemplate restTemplate = new RestTemplate();
      ObjectMapper objectMapper = new ObjectMapper();

      try {
         // 젬마에게 보낼 질문 페이로드 생성
         ObjectNode requestPayload = objectMapper.createObjectNode();
         requestPayload.put("model", "gemma3:4b");
         requestPayload.put("prompt",
    "다음 문장은 사용자가 실제로 '날씨나 기상 상태에 대한 정보를 알고 싶어서 한 질문'인지 판단해 주세요. " +
    "말의 의도상 날씨 상황이나 이유를 알고 싶어 한다면 'HeyYES', 그렇지 않으면 'SadlyNO'로만 답하세요. " +
    "단순한 감정 표현이나 일상 묘사는 제외합니다. 예보, 강수량, 강수확률, 미세먼지(대기질) 등도 날씨 정보입니다. \n문장: " + userQuestion);

         requestPayload.put("stream", false); // 스트리밍 비활성화

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestPayload), headers);

         // 젬마에게 요청 보내기
         String ollamaResponse = restTemplate.postForObject(ollamaApiUrl, requestEntity, String.class);

         // 젬마의 응답 파싱
         ObjectNode responseJson = (ObjectNode) objectMapper.readTree(ollamaResponse);
         String actualResponse = responseJson.get("response").asText();

         // 터미널에 젬마의 대답 출력
         System.out.println("젬마의 대답: " + actualResponse);

         if (actualResponse.contains("HeyYES")) {
            return chatController.chatRequestHandler(userQuestion);
         } else {
            return "저는 날씨 관련 질문에만 답변할 수 있습니다.";
         }
      } catch (Exception e) {
         System.err.println("Ollama API 호출 중 오류 발생함: " + e.getMessage());
         return "죄송합니다, 젬마와 통신 중 오류가 발생했습니다.";
      }
   }
}