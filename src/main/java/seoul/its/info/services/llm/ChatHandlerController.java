package seoul.its.info.services.llm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import seoul.its.info.services.llm.dto.ChatHandlerRequestDto;
import seoul.its.info.services.llm.dto.ChatHandlerResponseDto;
import seoul.its.info.services.llm.service.ChatHandlerService;

@RestController
public class ChatHandlerController {
   @Autowired
   private ChatHandlerService chatHandlerService;

   @PostMapping("/api/chat/handler")
   public ChatHandlerResponseDto.ChatResponse handleChatRequest(@RequestBody ChatHandlerRequestDto.ChatRequest request) {
       return chatHandlerService.handleGenericChat(request.getSystemPrompt(), request.getReferenceData(), request.getUserQuestion());
   }

   @PostMapping("/api/chat/dataAnalyze")
   public ChatHandlerResponseDto.ChatResponse handleDataAnalyzeRequest(@RequestBody ChatHandlerRequestDto.DataAnalyzeRequest request) {
       return chatHandlerService.handleDataAnalyzeRequest(request);
   }

   @GetMapping("/api/chat/dataAnalyze-stream")
   public SseEmitter handleDataAnalyzeStream(@RequestParam String userQuestion) {
       return chatHandlerService.handleDataAnalyzeStream(userQuestion);
   }
}