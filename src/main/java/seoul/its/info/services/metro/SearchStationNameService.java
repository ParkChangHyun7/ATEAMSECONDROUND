package seoul.its.info.services.metro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchStationNameService {
	
	private final MetroCacheManager metroCacheManager;
	
	public void execution(Model model) {
		
		String searchValue = (String) model.getAttribute("searchValue");

		// 결과를 담을 Map 객체 생성
		List<Map> matchingRows = new ArrayList<>();

		
		
		// 지하철역 검색
		List<Map> rows = metroCacheManager.getAllStationInfo();
		
		for (Map list : rows) {
			String bldnNm = (String) list.get("BLDN_NM");

			if (bldnNm != null && bldnNm.contains(searchValue)) {
				matchingRows.add(list); // 여러 개의 매칭된 row를 리스트에 저장
				
			}
		}

		model.addAttribute("list", matchingRows);

	}

	


}
