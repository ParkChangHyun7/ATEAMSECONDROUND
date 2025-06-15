package seoul.its.info.services.llm;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.Part;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.Tool;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatController {

   String GOOGLE_API_KEY = "AIzaSyA5s1y1QpVFc4MjS5YRdDrkFtjB04-KHXI";

   public String chatRequestHandler(String userMessage) {
      try {
         Client client = Client.builder().apiKey(GOOGLE_API_KEY).build();

         Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();

         GenerateContentConfig config = GenerateContentConfig.builder()
               .temperature(0.3f)
               .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0).build())
               .maxOutputTokens(300)
               .topP(0.9f)
               .topK(40f)
               .stopSequences(Arrays.asList("STOP", "\n\n"))
               .tools(ImmutableList.of(googleSearchTool))
               .build();

         List<Content> contents = new ArrayList<>();

         String currentKoreanTime = getCurrentKoreanTime();

         contents.add(Content.builder()
               .parts(Arrays.asList(Part.builder().text(
                     "이것은 api key holder의 pre-included 메시지 입니다. 당신의 역할은 날씨 정보 제공으로 엄격히 제한합니다. 날씨 질문에는 예보, 미세먼지, 기상상태, 대기질, 강수확률 등이 포함됩니다. 구글 검색 결과로 실제 날씨 정보를 알려주세요. 날씨 질문이 아닌 경우 사용자에게 날씨 질문만 대답할 수 있음을 알려주세요.")
                     .build()))
               .role("user")
               .build());

         contents.add(Content.builder()
               .parts(Arrays.asList(Part.builder()
                     .text(currentKoreanTime + "서비스 사용자의 채팅창 입력 내용 {}안에 포함됩니다. 단순 텍스트로 된 줄 나눔 없는 답변만 처리 가능하다는 점을 기억하세요. = {"
                           + userMessage + "}")
                     .build()))
               .role("user")
               .build());

         GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash-preview-05-20", contents,
               config);

         System.out.println(response.text());
         return response.text();
      } catch (Exception e) {
         System.err.println("Gemini API 호출 중 오류 발생: " + e.getMessage());
         return "죄송합니다. 챗봇 응답 처리 중 오류가 발생했습니다.";
      }
   }

   public String getCurrentKoreanTime() {
      // 한국 시간대 (KST, UTC+9) 설정
      ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");

      // 현재 한국 시간 가져오기
      LocalDateTime nowInKorea = LocalDateTime.now(koreaZoneId);

      // 원하는 형식으로 포맷
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String formattedDateTime = nowInKorea.format(formatter);

      // 최종 메시지 구성
      return "'UTC+9 기준 한국의 현재 시간은 " + formattedDateTime + " 입니다.'";
   }
}
