package seoul.its.info.services.traffic.parking.controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class ParkingController{

    /**
     *  공영주차장 지도 페이지 진입 처리
     * 
     * -클라이언트가 "/parking"주소로 GET 요청을 보내면 실행됨
     * -base.jsp 레이아웃을 기본으로 사용하고,
     *  그 안에 필요한 JSP페이지,리소스,스크립트 파일을 각각 include 시킴
     *
     * @param model JSP에 데이터를 전달하기 위한 Model객체
     * @return base.jsp(통합 레이아웃)
     */
    @GetMapping("/parking") //주소: http://localhost:포트번호/parking
    public String parkingPage(Model model){

        //페이지 제목:base.jsp에서 <title>${pageTitle}</title>등에 사용 가능
        model.addAttribute("pageTitle", "서울시 공영주차장 지도");

        //본문 JSP 페이지 경로 (contentPage에 해당)
        //base.jsp에서 <jsp:include page="${contentPage}"/>로 포함됨
        model.addAttribute("contentPage","content_pages/traffic/parking/parking.jsp");

        //JS,CSS 등 외부 리소스 경로(resourcePage)
        //base.jsp에서 <c:import url="${resourcesPage}"/>로 include됨
        model.addAttribute("resourcesPage","include/traffic/parking/resources.jsp");
        //주차장 지도 로직이 담긴 JS파일 경로 (scriptsPage)
        //base.jsp에서 <c:import url="${scriptsPage}" /> 로 include됨
        model.addAttribute("scriptsPage","include/traffic/parking/scripts.jsp");

        //최종적으로 base.jsp가 렌더링됨 (통합 레이아웃)
        return "base";
    }

}