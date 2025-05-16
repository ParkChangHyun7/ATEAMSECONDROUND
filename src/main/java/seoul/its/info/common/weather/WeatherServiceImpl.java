package seoul.its.info.common.weather;

import org.springframework.stereotype.Service;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class WeatherServiceImpl implements WeatherService {
    private WeatherDTO currentWeather;
    private static final String WEATHER_DATA_PATH = "src/main/data/api/json/seoulpublicdata.json";

    public WeatherServiceImpl() {
        updateWeatherData();
    }

    @Override
    public WeatherDTO getCurrentWeather() {
        return currentWeather;
    }

    @Override
    public void updateWeatherData() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(
                new InputStreamReader(
                    new FileInputStream(WEATHER_DATA_PATH), 
                    StandardCharsets.UTF_8
                )
            );
            JSONObject cityData = (JSONObject) jsonData.get("CITYDATA");
            JSONObject weatherStts = (JSONObject) ((JSONArray) cityData.get("WEATHER_STTS")).get(0);
            JSONObject fcst24hours = (JSONObject) ((JSONArray) weatherStts.get("FCST24HOURS")).get(0);
            // json 파일에서 첫번째 weather_stts 를 가져온 뒤 그 안에 fcst24hours의 값을 가지고 옴.

            WeatherDTO weather = new WeatherDTO();
            weather.setTemp(Double.parseDouble(weatherStts.get("TEMP").toString()));
            weather.setRainStatus(weatherStts.get("PRECPT_TYPE").toString());
            weather.setSkyStatus(fcst24hours.get("SKY_STTS").toString());
            weather.setPm10(Integer.parseInt(weatherStts.get("PM10").toString()));
            weather.setPm25(Integer.parseInt(weatherStts.get("PM25").toString()));
            weather.setLastUpdated(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
            // weatherstts에서 temp, pm10, pm25 추출 후 set으로 메모리, fcst24hours에서 sky_stts 추출 후 set으로 메모리함.

            currentWeather = weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 