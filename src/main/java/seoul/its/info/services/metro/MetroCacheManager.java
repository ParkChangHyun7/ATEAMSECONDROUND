package seoul.its.info.services.metro;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableCaching
@SuppressWarnings("unchecked")
public class MetroCacheManager {


	private final MetroApiReturn metroApiReturn;

	@Value("${seoul.metro.api.key}")
	private String seoulMetroKey;
	
	
	//모든 지하철역 정보 불러오기
	//캐시에 모든 데이터 저장
	@Cacheable(value = "stationInfo")
	public List<Map<String, Object>> getAllStationInfo(){
		
		String searchUrl = "http://openapi.seoul.go.kr:8088/" + seoulMetroKey
		+ "/json/subwayStationMaster/1/1000/";
		
		
		System.out.println("캐시가 없으므로 호출함");
	
		
		Map<String, Object> resultData = metroApiReturn.api(searchUrl).block();
		
		Map<String, Object> items = (Map<String, Object>) resultData.get("subwayStationMaster");
		
		List<Map<String, Object>> rows = (List<Map<String, Object>>) items.get("row");
		
		return rows;
	}
	
	
	
	
	
}
