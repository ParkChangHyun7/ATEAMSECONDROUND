package seoul.its.info.services.llm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class ChatHandlerRequestDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        private String systemPrompt;
        private String referenceData;
        private String userQuestion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataAnalyzeRequest {
        private String referenceData;
        private String userQuestion;
    }
}