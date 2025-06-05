package seoul.its.info.services.traffic.busstop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class BusStopController {
    @Autowired
    private BusStopService service;

    @GetMapping("/api/busstops")
    public List<BusStop> getStops() {
        return service.getAllStops();
    }

    @GetMapping("/api/load")
    public String loadData() throws Exception {
        service.loadBusStops();
        return "Loaded";
    }    
    
    @GetMapping("/api/busstops/search")
    public List<BusStop> searchStops(@RequestParam String keyword) {
        return service.searchByName(keyword);
    }
    
    @GetMapping("/api/busroutes/{stId}")
    public List<BusRouteInfo> getRoutesByStation(@PathVariable String stId) throws Exception {
        return service.getRoutesByStation(stId);
    }
   

}
