package seoul.its.info.services.traffic.busstop;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bus_stop")
@Data
public class BusStop {

    @Id
    private String arsId;
    private String stId;
    
    private String name;
    private double latitude;
    private double longitude;
    
}

