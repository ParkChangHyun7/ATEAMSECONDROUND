package seoul.its.info.common.weather;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherScheduler {
    private final WeatherService weatherService;

    public WeatherScheduler(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Scheduled(cron = "0 1 * * * *") // 매시간 정각+1분에 실행
    // Json 파일을 매시간 정각에 업데이트 하도록 스케쥴링 해놨기 때문에 날씨는 안정성 위해 +1분으로 실행
    public void updateWeatherData() {
        weatherService.updateWeatherData();
    }
} 