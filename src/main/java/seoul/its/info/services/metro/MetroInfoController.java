package seoul.its.info.services.metro;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Controller
public class MetroInfoController {


    @GetMapping("/metro/accidents")
    public String ShowAccidentsChart(Model model)throws JsonProcessingException {
        List<Map<String, Object>> accidentData = List.of(

        Map.of("year","2019","type","출입문","count",3),
        Map.of("year","2020","type","출입문","count",5),
        Map.of("year","2020","type","출입문","count",7),
        Map.of("year","2020","type","출입문","count",6),
        Map.of("year","2020","type","출입문","count",4)

        );

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute ("accidents", mapper.writeValueAsString(accidentData));
        model.addAttribute("pageTitle","지하철 출입문 사고 현황");
        model.addAttribute("contentPage","content_pages/metro/accident/accidents.jsp");
        model.addAttribute("resourcesPage","include/metro/accident/resources.jsp" );
        model.addAttribute("scriptsPage", "include/metro/accident/scripts.jsp");

        return "base";
    }
    

}