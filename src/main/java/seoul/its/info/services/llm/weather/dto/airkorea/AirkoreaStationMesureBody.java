package seoul.its.info.services.llm.weather.dto.airkorea;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirkoreaStationMesureBody {
    private int totalCount;
    private List<AirkoreaStationMesureItem> items;
    private int pageNo;
    private int numOfRows;
} 