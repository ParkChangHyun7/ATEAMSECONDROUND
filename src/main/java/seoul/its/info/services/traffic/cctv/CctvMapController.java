package seoul.its.info.services.traffic.cctv;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/traffic/cctv")
public class CctvMapController {

    @GetMapping("/map")
    public String getCctvMap() {
        return "cctvmap"; // src/main/resources/static/cctvmap.html을 의미
    }
}