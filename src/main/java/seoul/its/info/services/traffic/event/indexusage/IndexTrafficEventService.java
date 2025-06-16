package seoul.its.info.services.traffic.event.indexusage;

import java.util.concurrent.CompletableFuture;
import java.util.Map;

public interface IndexTrafficEventService {
    CompletableFuture<Void> processAndSaveTrafficEvents();

    CompletableFuture<Map<String, String>> fetchTrafficSpeedStats();
}