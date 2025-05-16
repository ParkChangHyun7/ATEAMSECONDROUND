package seoul.its.info.common.weather;

import lombok.Data;

@Data
public class WeatherDTO {
    private double temp;
    private String skyStatus;
    private String rainStatus;
    private int pm10;
    private int pm25;
    private long lastUpdated;
} 