package seoul.its.info.services.boards.posts.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

@Service
@Slf4j
public class ViewCountManager {

    private final Map<Long, Long> viewTimestampCache = new ConcurrentHashMap<>(); // 조회 기록 캐시
    private final long VIEW_INCREMENT_THRESHOLD_MS = 3 * 60 * 60 * 1000; // 3시간 (밀리초)

    /**
     * 특정 게시글의 조회수를 증가시킬지 여부를 확인하고 캐시를 업데이트하는 메서드
     *
     * @param postId 게시글 ID
     * @return 조회수를 증가시켜야 하면 true, 아니면 false
     */
    public boolean shouldIncrementViewCount(Long postId) {
        long currentTime = System.currentTimeMillis();
        Long lastViewTime = viewTimestampCache.get(postId);

        // 캐시에 없거나 3시간이 지났으면 조회수 증가 필요
        if (lastViewTime == null || (currentTime - lastViewTime) >= VIEW_INCREMENT_THRESHOLD_MS) {
            viewTimestampCache.put(postId, currentTime);
            return true;
        }
        return false;
    }

    
    @Scheduled(fixedRate = 3600000) // 1시간(3600000 밀리초) 마다 실행
    public void cleanupViewTimestampCache() {
        long cutoffTime = System.currentTimeMillis() - VIEW_INCREMENT_THRESHOLD_MS;
        Iterator<Map.Entry<Long, Long>> iterator = viewTimestampCache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, Long> entry = iterator.next();
            if (entry.getValue() < cutoffTime) {
                iterator.remove(); // 만료된 항목 제거
            }
        }
        log.info("조회수 캐시 청소 완료. 현재 크기: {}", viewTimestampCache.size());
    }
}
