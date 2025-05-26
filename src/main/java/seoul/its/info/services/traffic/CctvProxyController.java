package seoul.its.info.services.traffic;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/cctv-proxy")
public class CctvProxyController {

    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
            .build();

    @GetMapping
    public void proxyCctv(@RequestParam String url, HttpServletResponse response) {
        try {
            byte[] body = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.isError(), res -> Mono.error(new RuntimeException("접속 실패")))
                    .bodyToMono(byte[].class)
                    .block();

            if (url.contains(".m3u8")) {
                String base = url.substring(0, url.lastIndexOf("/") + 1);
                String playlist = new String(body, "UTF-8");

                Pattern pattern = Pattern.compile("(?m)^([^#\\n]+)$");
                Matcher matcher = pattern.matcher(playlist);

                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String segmentUrl = matcher.group(1).trim();
                    String fullUrl = segmentUrl.startsWith("http") ? segmentUrl : base + segmentUrl;
                    String proxyUrl = "/cctv-proxy?url=" + URLEncoder.encode(fullUrl, "UTF-8");
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(proxyUrl));
                }
                matcher.appendTail(sb);

                response.setContentType("application/vnd.apple.mpegurl");
                response.getOutputStream().write(sb.toString().getBytes("UTF-8"));
            } else {
                response.setContentType("video/mp2t");
                OutputStream os = response.getOutputStream();
                os.write(body);
                os.flush();
            }

        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
        }
    }
}
