package seoul.its.info.services.llm.weather.dto.weather;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class WeatherInfo {
    private String city;
    private String currentWeatherDescription;
    private double temperature;
    private String skyCondition;
    private String precipitationType;
    private double windSpeed;
    private String vilageForecastDescription;
    private String midTermForecastDescription;
    private double pm10Value;
    private String pm10Grade;
    private double pm25Value;
    private String pm25Grade;
    // 추가적으로 단기, 중장기 예보, 미세먼지 정보 등을 포함할 수 있음
    // 예시: List<DailyForecast> dailyForecasts;
    // 예시: FineDustInfo fineDustInfo;
} 