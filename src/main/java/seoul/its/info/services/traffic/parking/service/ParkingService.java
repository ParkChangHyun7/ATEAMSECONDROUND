package seoul.its.info.services.traffic.parking.service;

import seoul.its.info.services.traffic.parking.dto.PublicParkingDto;
import java.util.List;
/**
 * 주차장 관련 서비스 로직을 정의한 인터페이스
 */

public interface ParkingService {

    /**
     * 공영 주차장 목록 전체를 반환하는 메서드
     * @return PublicParkingDto리스트
     */
    List<PublicParkingDto>getAllPublicParking();
}
