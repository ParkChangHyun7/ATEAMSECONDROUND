package seoul.its.info.services.traffic;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class CctvProxyController {

    @GetMapping("/cctv-proxy")
    public void proxy(@RequestParam String url, HttpServletResponse response) throws IOException {
        HttpURLConnection conn = null;
        try {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
            System.out.println("프록시 요청 URL: " + decodedUrl);

            URL targetUrl = new URL(decodedUrl);
            conn = (HttpURLConnection) targetUrl.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP 오류: " + responseCode);
            }

            String contentType = conn.getContentType();
            if (contentType != null) {
                response.setContentType(contentType);
            }

            response.setHeader("Access-Control-Allow-Origin", "*");

            InputStream is = conn.getInputStream();
            OutputStream os = response.getOutputStream();

            // ✅ .m3u8 확장자가 없어도 Content-Type으로 판단
            boolean isM3u8 = contentType != null && contentType.contains("application/vnd.apple.mpegurl");

            if (isM3u8) {
                String base = decodedUrl.substring(0, decodedUrl.lastIndexOf("/") + 1);
                String playlist = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("📺 m3u8 내용 원본:\n" + playlist);

                Pattern pattern = Pattern.compile("(?m)^([^#\\n]+)$");
                Matcher matcher = pattern.matcher(playlist);

                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String segment = matcher.group(1).trim();

                    if (segment.startsWith("#")) {
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(segment));
                        continue;
                    }

                    if (segment.contains("/cctv-proxy?url=") || segment.contains("localhost:9998")) {
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(segment));
                        continue;
                    }

                    String fullUrl = segment.startsWith("http")
                        ? segment
                        : base + segment.replaceFirst("^/+", "");

                    String proxyUrl = "/cctv-proxy?url=" + URLEncoder.encode(fullUrl, StandardCharsets.UTF_8);
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(proxyUrl));
                }

                matcher.appendTail(sb);

                response.setContentType("application/vnd.apple.mpegurl");
                os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            } else {
                is.transferTo(os);
            }

            os.flush();
        } catch (Exception e) {
            System.out.println("🚨 프록시 오류 발생: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
