// package seoul.its.info.services.traffic.busstop;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.w3c.dom.*;
// import javax.xml.parsers.*;
// import java.net.URL;
// import java.util.ArrayList;
// import java.util.List;


// import org.springframework.transaction.annotation.Transactional;


// @Service
// public class BusStopService {
//     @Autowired
//     private BusStopRepository repository;

//     @Transactional
//     public void loadBusStops() throws Exception {
//         int savedCount = 0;
//         ObjectMapper mapper = new ObjectMapper();

//         for (int start = 1; start <= 12000; start += 1000) {
//             int end = start + 999;
//             String apiUrl = "http://openapi.seoul.go.kr:8088/555978545472797533354d76547546/json/busStopLocationXyInfo/" + start + "/" + end + "/";
//             System.out.println("🚍 요청: " + apiUrl);

//             JsonNode root = mapper.readTree(new URL(apiUrl));
//             JsonNode rows = root.path("busStopLocationXyInfo").path("row");

//             for (JsonNode node : rows) {
//                 String arsId = node.path("STOPS_NO").asText();
//              // String stId = node.path("STOP_ID").asText();    // TODO: 버스 노선 정보용으로 추후 사용
//                 String name = node.path("STOPS_NM").asText();
//                 double longitude = node.path("XCRD").asDouble();
//                 double latitude = node.path("YCRD").asDouble();

//                 if (arsId.isEmpty() || name.isEmpty()) {
//                     System.out.println("누락된 데이터 생략");
//                     continue;
//                 }

//                 if (!repository.existsById(arsId)) {
//                     BusStop stop = new BusStop();
//                     stop.setArsId(arsId);
//                     stop.setName(name);
//                     stop.setLatitude(latitude);
//                     stop.setLongitude(longitude);
//                     repository.save(stop);
//                     savedCount++;
//                 }
//             }

//             Thread.sleep(500); // 과도한 요청 방지
//         }

//         System.out.println("✅ 최종 저장된 정류장 수: " + savedCount);
//     }


//     public List<BusStop> getAllStops() {
//         return repository.findAll();
//     }    
    
//     public List<BusStop> searchByName(String keyword) {
//         return repository.findByNameContaining(keyword);
//     }



// 	public List<BusRouteInfo> getRoutesByStation(String stId) throws Exception {
// 	    String url = "http://ws.bus.go.kr/api/rest/stationinfo/getRouteByStation?serviceKey=YOUR_ENCODED_KEY&stId=" + stId;
	    
// 	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
// 	    DocumentBuilder builder = factory.newDocumentBuilder();
// 	    Document doc = builder.parse(url);
	
// 	    NodeList rows = doc.getElementsByTagName("itemList");
// 	    List<BusRouteInfo> result = new ArrayList<>();
	
// 	    for (int i = 0; i < rows.getLength(); i++) {
// 	        Element el = (Element) rows.item(i);
// 	        String routeId = el.getElementsByTagName("busRouteId").item(0).getTextContent();
// 	        String routeName = el.getElementsByTagName("busRouteNm").item(0).getTextContent();
	
// 	        BusRouteInfo info = new BusRouteInfo();
// 	        info.setRouteId(routeId);
// 	        info.setRouteName(routeName);
// 	        result.add(info);
// 	    }
	
// 	    return result;
// 	}
// }


