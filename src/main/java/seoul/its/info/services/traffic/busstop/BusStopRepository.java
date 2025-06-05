package seoul.its.info.services.traffic.busstop;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface BusStopRepository extends JpaRepository<BusStop, String> {
    List<BusStop> findByNameContaining(String keyword);
    
    boolean existsById(String arsId);
}

