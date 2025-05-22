package seoul.its.info.services.traffic.parking.dto;

/**
 *  공영/민간 주차장 정보를 담는 데이터 전송 객체(DTO)
 * -Controller <-> Service <-> View사이에서 데이터 전달용으로 사용됨
 */


public class PublicParkingDto {

    private String name;     //주차장명
    private String type;     //주차장 유형: 공영 or 민간
    private String address;  //주소
    private double lat;      //위도
    private double lng;      //경도
    

    //기본 생성자
    public PublicParkingDto(){}

    //전체 필드를 초기화하는 생성자
    public PublicParkingDto(String name, String type, String address, double lat, double lng){
        this.name = name;
        this.type = type;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    //Getter & Setter 메서드
    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getType(){return type;}
    public void setType(String type){this.type = type;}

    public String getAddress(){return address; }
    public void setAddress(String address){this.address = address;}

    public double getLat(){return lat;}
    public void setLat(double lat){this.lat = lat;}

    public double getLng(){return lng;}
    public void setLng(double lng){this.lng=lng;}
}
