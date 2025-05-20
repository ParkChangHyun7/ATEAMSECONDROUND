//ParkingServiceImpl.java
//주차장 정보 기능을 실제로 구현한 클래스

package seoul.its.info.services.traffic.parking.service;

import org.springframework.stereotype.Service;
import seoul.its.info.services.traffic.parking.dto.ParkingDto;

import java.util.ArrayList;
import java.util.List;

@Service //Service역할을 나타내는 Spring 어노테이션
public class ParkingServiceImpl implements ParkingService {

    @Override
    public List<ParkingDto>getAllParkingLots(){
        //실제로는 DB또는 API에서 받아야 하지만,여기선 예시용 하드코딩
        List<ParkingDto>list = new ArrayList<>();

        ParkingDto lot1 = new ParkingDto();
        lot1.setName("서울시청 주차장");
        lot1.setLat(37.5665);
        lot1.setLng(126.9780);
        
        ParkingDto lot2 = new ParkingDto();
        lot2.setName("을지로입구역 공영주차장");
        lot2.setLat(37.5678);
        lot2.setLng(126.9825);
        
        list.add(lot1);
        list.add(lot2);

        return list;
    }
}


