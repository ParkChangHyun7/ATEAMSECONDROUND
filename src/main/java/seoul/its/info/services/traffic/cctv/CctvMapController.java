
 package seoul.its.info.services.traffic.cctv;
 
 import org.springframework.stereotype.Controller; import
 org.springframework.ui.Model; import
 org.springframework.web.bind.annotation.GetMapping;
 


@Controller
class CctvMapController {
    @GetMapping("/traffic/cctvMap")
    public String showCctvMap(Model model) {
        model.addAttribute("contentPage", "content_pages/traffic/cctvMap.jsp");
        model.addAttribute("scriptsPage", "include/traffic/cctvMap/scripts.jsp");
        model.addAttribute("resourcesPage", "include/traffic/cctvMap/resources.jsp");
        return "base"; // base.jsp 사용 시
    }
}





/*
 * @Controller
 * 
 * @RequestMapping("/traffic") public class CctvMapController {
 * 
 * @GetMapping("/cctvMap") public String showCctvMap(Model model) {
 * model.addAttribute("pageTitle", "CCTV 지도");
 * 
 * // 📍 실제 JSP include 경로들 model.addAttribute("contentPage",
 * "content_pages/traffic/cctvMap.jsp"); model.addAttribute("resourcesPage",
 * "include/traffic/cctvMap/resources.jsp"); model.addAttribute("scriptsPage",
 * "include/traffic/cctvMap/scripts.jsp");
 * 
 * return "base"; // base.jsp에서 contentPage 등을 include } }
 * 
 * 
 * package seoul.its.info.services.traffic.cctv;
 * 
 * import org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RequestMapping;
 * 
 * @Controller
 * 
 * @RequestMapping("/traffic") public class CctvMapController {
 * 
 * @GetMapping("/cctvMap") public String showCctvMap(Model model) {
 * model.addAttribute("pageTitle", "CCTV 지도");
 * 
 * // 📍 실제 파일 경로에 맞게 변경 model.addAttribute("contentPage",
 * "content_pages/traffic/cctvMap.jsp"); model.addAttribute("resourcesPage",
 * "include/traffic/cctvMap/resources.jsp"); model.addAttribute("scriptsPage",
 * "include/traffic/cctvMap/scripts.jsp");
 * 
 * return "base"; } }
 */