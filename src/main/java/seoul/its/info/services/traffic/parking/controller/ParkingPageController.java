package seoul.its.info.services.traffic.parking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 주차장 페이지(JSP)진입용 Controller
 * 이 컨트롤러는 브라우저에서 URL을 입력했을 때 JSP화면을 보여주는 역할
 * JSON데이터를 응답한는게 아니라 JSP화면을 리턴하는게 목적
 */

@Controller
public class ParkingPageController {

    /**
     * 최초 진입 URL: http://localhost:9998/parking
     * -Jsp 화면을 띄우기 위한 요청
     * model에 contentPage값을 넣어 base.jsp가 해당 JSP를 include하도록 함
     * 
     * @param model -JSP include 대상 설정을 위핸 Model객체
     * @return base.jsp로 이동하여 ,contentPage속성에 따라 실제 화면 로딩
     */
    @GetMapping("/parking")
    public String parkingPage(Model model ) {
        //base.jsp에서 <jsp:include page="${contentPage}"/>형태로 사용될 경로
        model.addAttribute("contentPage", "content_pages/parking/parking.jsp");

        //base.jsp안에 위에서 설정한 contentPage를 include
        return "base";// => views/base.jsp
    }
}
