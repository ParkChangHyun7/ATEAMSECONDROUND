package seoul.its.info.services.traffic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/traffic")
public class TrafficEventsProvider {

	@Value("${its.api.key}")
   private String apiKey;

   @GetMapping("/events")
   public ResponseEntity<String> getTrafficEvents() {
      String url = "https://openapi.its.go.kr:9443/eventInfo?apiKey=" + apiKey + "&type=all&eventType=all&getType=json";
      try {
         RestTemplate restTemplate = new RestTemplate();
         String response = restTemplate.getForObject(url, String.class);
         return ResponseEntity.ok(response);
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 호출 실패");
      }
   }
   
   // 🔹 CCTV 정보
   @GetMapping("/cctv")
   public ResponseEntity<String> getCctvInfo() {
       String url = "https://openapi.its.go.kr:9443/cctvInfo"
               + "?apiKey=" + apiKey
               + "&type=its"
               + "&cctvType=1"
               + "&minX=126.76"
               + "&maxX=127.20"
               + "&minY=37.40"
               + "&maxY=37.70"
               + "&getType=json";
       try {
           RestTemplate restTemplate = new RestTemplate();
           String response = restTemplate.getForObject(url, String.class);
           return ResponseEntity.ok(response);
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CCTV API 호출 실패");
       }
   }
   
   
   
   
   

}