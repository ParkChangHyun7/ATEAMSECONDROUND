package seoul.its.info.services.traffic.event.indexusage;

import java.util.concurrent.CompletableFuture;

public interface IndexTrafficEventService {
    CompletableFuture<Void> processAndSaveTrafficEvents();
} 