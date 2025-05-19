//ParkingDto.java
//주차장 정보를 담는 Data Transfer Object(DTO)

package seoul.its.info.services.traffic.parking.dto;

import lombok.Data;

@Data //lombok 어노테이션으로 getter/setter, toString, equals 등을 자동 생성
public class ParkingDto {

    //주차장 이름
    private String name;
    //주차장 위도 좌표
    private double lat;
    //주차장 경도 좌표
    private double lng;
}
