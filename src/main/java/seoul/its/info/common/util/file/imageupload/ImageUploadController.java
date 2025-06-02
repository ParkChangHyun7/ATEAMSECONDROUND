package seoul.its.info.common.util.file.imageupload;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

   private final ImageUploadService imageUploadService;

   @PostMapping("/api/upload/image") // API 경로 일관성 확보
   public ResponseEntity<?> uploadImage(
         @RequestParam("image") MultipartFile file,
         @RequestParam("uploadFrom") int uploadFrom,
         @RequestParam("parentId") Long parentId,
         @AuthenticationPrincipal UserDetails userDetails,
         HttpServletRequest request) {

      if (userDetails == null) {
          return ResponseEntity.status(401).body(Map.of("message", "인증되지 않은 사용자입니다."));
      }

      UserDetailsImpl principal = (UserDetailsImpl) userDetails;
      Long userId = principal.getId(); 
      String loginId = principal.getUsername(); 
      String uploaderIpAddress = request.getRemoteAddr();

      try {
          String imageUrl = imageUploadService.uploadImage(file, uploadFrom, parentId, userId, loginId, uploaderIpAddress);

          Map<String, Object> responseBody = new HashMap<>();
          responseBody.put("message", "이미지 업로드 성공");
          responseBody.put("imageUrl", imageUrl);

          return ResponseEntity.ok(responseBody);
      } catch (RuntimeException e) {
          System.err.println("Image upload failed: " + e.getMessage());
          // e.printStackTrace(); // 운영 환경에서는 상세 스택 트레이스를 클라이언트에게 노출하지 않는 것이 좋습니다.
          return ResponseEntity.badRequest().body(Map.of("message", "이미지 업로드 실패: " + e.getMessage()));
      } catch (Exception e) {
          System.err.println("Server error during image upload: " + e.getMessage());
          // e.printStackTrace();
          return ResponseEntity.status(500).body(Map.of("message", "서버 내부 오류로 이미지 업로드에 실패했습니다."));
      }
   }
}