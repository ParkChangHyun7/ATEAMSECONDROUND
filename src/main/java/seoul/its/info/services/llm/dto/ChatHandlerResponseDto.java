package seoul.its.info.services.llm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ChatHandlerResponseDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponse {
        private boolean success;
        private String response;
        private String error;
        
        public static ChatResponse success(String response) {
            return new ChatResponse(true, response, null);
        }
        
        public static ChatResponse error(String error) {
            return new ChatResponse(false, null, error);
        }
    }
}
