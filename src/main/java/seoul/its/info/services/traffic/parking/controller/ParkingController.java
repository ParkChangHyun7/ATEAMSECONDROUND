package seoul.its.info.services.traffic.parking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ParkingController {

    //  카카오 지도 API 키 주입
    @Value("${kakao.api.js.key}")
    private String kakaoApiKey;

    @GetMapping(value = "/parking", produces = "application/json; charset=UTF-8")
    public String parkingPage(Model model) {
        //  모델에 키를 함께 담아야 resources.jsp에서 ${kakaoApiKey}로 사용 가능
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        model.addAttribute("pageTitle", "서울시 공영주차장 지도");
        model.addAttribute("contentPage", "content_pages/traffic/parking/map.jsp");
        model.addAttribute("resourcesPage", "include/traffic/parking/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/parking/scripts.jsp");
        return "base";
    }
}
