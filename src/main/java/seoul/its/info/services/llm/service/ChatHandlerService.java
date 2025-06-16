package seoul.its.info.services.llm.service;

import seoul.its.info.services.llm.dto.ChatHandlerRequestDto;
import seoul.its.info.services.llm.dto.ChatHandlerResponseDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatHandlerService {
    
    String callGemmaModel(String systemPrompt, String referenceData, String userQuestion);
    
    ChatHandlerResponseDto.ChatResponse handleChatRequest(ChatHandlerRequestDto.ChatRequest request);
    
    ChatHandlerResponseDto.ChatResponse handleDataAnalyzeRequest(ChatHandlerRequestDto.DataAnalyzeRequest request);

    ChatHandlerResponseDto.ChatResponse handleGenericChat(String systemPrompt, String referenceData, String userQuestion);

    SseEmitter handleDataAnalyzeStream(String userQuestion);
}
