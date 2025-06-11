package seoul.its.info.services.llm.weather.dto.airkorea;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirkoreaStationMesureHeader {
    private String resultCode;
    private String resultMsg;
} 