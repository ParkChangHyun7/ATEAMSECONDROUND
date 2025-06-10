package seoul.its.info.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ProfanityFilter {

    private Set<String> badWords = new HashSet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("data/constant/json/badwords.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<String> words = objectMapper.readValue(inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                badWords.addAll(words);
                log.info("금칙어 리스트 로드 완료. {}개 단어 로드됨.", badWords.size());
            }
        } catch (IOException e) {
            log.error("금칙어 파일(badwords.json) 로드 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public boolean containsProfanity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        String lowerCaseText = text.toLowerCase();
        for (String word : badWords) {
            if (lowerCaseText.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstProfanity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String lowerCaseText = text.toLowerCase();
        for (String word : badWords) {
            if (lowerCaseText.contains(word)) {
                return word;
            }
        }
        return null;
    }
} 