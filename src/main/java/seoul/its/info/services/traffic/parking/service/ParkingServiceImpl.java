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
    public List<ParkingDto>getParkingList(){
        List<ParkingDto>list = new ArrayList<>();

        //임시 데이터 (예: 강남역, 시청 등 좌표)
        ParkingDto p1 = new ParkingDto();
        p1.setName("강남역 공영주차장");
        p1.setLat(37.4979);
        p1.setLng(127.0276);
        
        ParkingDto p2 = new ParkingDto();
        p2.setName("서울시청 주차장");
        p2.setLat(37.5665);
        p2.setLng(126.9780);
        
        list.add(p1);
        list.add(p2);

        return list;
    }
}


