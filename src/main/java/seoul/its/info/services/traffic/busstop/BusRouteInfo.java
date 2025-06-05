package seoul.its.info.services.traffic.busstop;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BusRouteInfo {
	
    @Id
    private String routeId;
    private String routeName;
    // getter/setter
}
