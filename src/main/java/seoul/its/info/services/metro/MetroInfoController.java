package seoul.its.info.services.metro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MetroInfoController {

    @Value("${kakao.bus.key}")
    private String kakaoBusKey;

    @GetMapping("/metro/info")
    public String metroInfo(Model model) {
        model.addAttribute("kakao", kakaoBusKey);
        return "epl/metro";
    }
}