package seoul.its.info.services.traffic.event.indexusage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/index")
public class IndexTrafficEventController {

    private final IndexTrafficEventService indexTrafficEventService;
    private final String jsonFilePath = "src/main/data/api/json/seoulTrafficEvent.json";

    public IndexTrafficEventController(IndexTrafficEventService indexTrafficEventService) {
        this.indexTrafficEventService = indexTrafficEventService;
    }

    @GetMapping("/trafficevent")
    public ResponseEntity<String> getTrafficEvents() {
        try {
            indexTrafficEventService.processAndSaveTrafficEvents();
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)),
                    java.nio.charset.StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/json; charset=UTF-8"));

            return new ResponseEntity<>(jsonContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching traffic events: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching traffic events", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/speedmeter")
    public ResponseEntity<Map<String, String>> getTrafficSpeedStats() {
        try {
            Map<String, String> speedStats = indexTrafficEventService.fetchTrafficSpeedStats().get();
            return new ResponseEntity<>(speedStats, HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching traffic speed stats: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}