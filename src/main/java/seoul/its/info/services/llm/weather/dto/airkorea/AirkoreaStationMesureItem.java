package seoul.its.info.services.llm.weather.dto.airkorea;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirkoreaStationMesureItem {
    private String dataTime;
    private String stationName;
    private double pm10Value;
    private double pm25Value;
    private String khaiGrade;
    private String pm10Grade;
    private String pm25Grade;
    private double o3Value;
    private String o3Grade;
    private double coValue;
    private String coGrade;
    private double no2Value;
    private String no2Grade;
    private double so2Value;
    private String so2Grade;
    private String mangName;
} 