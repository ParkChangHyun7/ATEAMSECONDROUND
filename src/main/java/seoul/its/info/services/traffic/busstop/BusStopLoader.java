package seoul.its.info.services.traffic.busstop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class BusStopLoader implements CommandLineRunner {

    @Autowired
    private BusStopService busStopService;

    @Override
    public void run(String... args) throws Exception {
        busStopService.loadBusStops();
    }
}

