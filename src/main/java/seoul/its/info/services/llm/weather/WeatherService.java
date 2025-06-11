package seoul.its.info.services.llm.weather;

import org.springframework.stereotype.Service;
import seoul.its.info.services.llm.weather.api.KmaUltraSrtNcstApiCaller;
import seoul.its.info.services.llm.weather.api.KmaVilageFcstApiCaller;
import seoul.its.info.services.llm.weather.api.KmaMidFcstApiCaller;
import seoul.its.info.services.llm.weather.api.AirkoreaStationMesureApiCaller;
import seoul.its.info.services.llm.weather.dto.kma.KmaUltraSrtNcstItem;
import seoul.its.info.services.llm.weather.dto.kma.KmaUltraSrtNcstResponse;
import seoul.its.info.services.llm.weather.dto.kma.KmaVilageFcstItem;
import seoul.its.info.services.llm.weather.dto.kma.KmaVilageFcstResponse;
import seoul.its.info.services.llm.weather.dto.kma.KmaMidFcstItem;
import seoul.its.info.services.llm.weather.dto.kma.KmaMidFcstResponse;
import seoul.its.info.services.llm.weather.dto.airkorea.AirkoreaStationMesureItem;
import seoul.its.info.services.llm.weather.dto.airkorea.AirkoreaStationMesureResponse;
import seoul.its.info.services.llm.weather.dto.weather.WeatherInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

@Service
public class WeatherService {

    private final KmaUltraSrtNcstApiCaller kmaUltraSrtNcstApiCaller;
    private final KmaVilageFcstApiCaller kmaVilageFcstApiCaller;
    private final KmaMidFcstApiCaller kmaMidFcstApiCaller;
    private final AirkoreaStationMesureApiCaller airkoreaStationMesureApiCaller;
    private final GeoService geoService;

    private static final Map<String, String> SKY_CODE_MAP = new HashMap<>();
    private static final Map<String, String> PTY_CODE_MAP = new HashMap<>();
    private static final Map<String, String> STN_ID_MAP = new HashMap<>();
    private static final Map<String, String> STATION_NAME_MAP = new HashMap<>();
    private static final Map<String, String> MAJOR_CITY_ALIASES = new HashMap<>();

    static {
        SKY_CODE_MAP.put("1", "맑음");
        SKY_CODE_MAP.put("3", "구름많음");
        SKY_CODE_MAP.put("4", "흐림");

        PTY_CODE_MAP.put("0", "없음");
        PTY_CODE_MAP.put("1", "비");
        PTY_CODE_MAP.put("2", "비/눈");
        PTY_CODE_MAP.put("3", "눈");
        PTY_CODE_MAP.put("4", "소나기");
        PTY_CODE_MAP.put("5", "빗방울");
        PTY_CODE_MAP.put("6", "빗방울눈날림");
        PTY_CODE_MAP.put("7", "눈날림");

        // 초기 기본값 설정 (만약 파일 로드 실패 시를 대비)
        STN_ID_MAP.put("서울", "108");
        STN_ID_MAP.put("인천", "112");
        STN_ID_MAP.put("춘천", "101");
        STN_ID_MAP.put("강릉", "105");
        STN_ID_MAP.put("대전", "133");
        STN_ID_MAP.put("청주", "131");
        STN_ID_MAP.put("부산", "159");
        STN_ID_MAP.put("대구", "143");
        STN_ID_MAP.put("광주", "156");
        STN_ID_MAP.put("제주", "184");
        STN_ID_MAP.put("울릉도", "115");

        STATION_NAME_MAP.put("서울", "종로");
        STATION_NAME_MAP.put("부산", "부산진");
        STATION_NAME_MAP.put("대구", "수성");
        STATION_NAME_MAP.put("인천", "남동");
        STATION_NAME_MAP.put("광주", "북구");
        STATION_NAME_MAP.put("대전", "유성구");
        STATION_NAME_MAP.put("울산", "남구");
        STATION_NAME_MAP.put("세종", "신흥동");
        STATION_NAME_MAP.put("경기", "수원");
        STATION_NAME_MAP.put("강원", "춘천");
        STATION_NAME_MAP.put("충북", "청주");
        STATION_NAME_MAP.put("충남", "천안");
        STATION_NAME_MAP.put("전북", "전주");
        STATION_NAME_MAP.put("전남", "목포");
        STATION_NAME_MAP.put("경북", "포항");
        STATION_NAME_MAP.put("경남", "창원");
        STATION_NAME_MAP.put("제주", "제주시");

        // 주요 도시 별칭 매핑
        MAJOR_CITY_ALIASES.put("서울", "서울");
        MAJOR_CITY_ALIASES.put("인천", "인천");
        MAJOR_CITY_ALIASES.put("수원", "경기");
        MAJOR_CITY_ALIASES.put("고양", "경기");
        MAJOR_CITY_ALIASES.put("성남", "경기");
        MAJOR_CITY_ALIASES.put("용인", "경기");
        MAJOR_CITY_ALIASES.put("안산", "경기");
        MAJOR_CITY_ALIASES.put("안양", "경기");
        MAJOR_CITY_ALIASES.put("부천", "경기");
        MAJOR_CITY_ALIASES.put("광명", "경기");
        MAJOR_CITY_ALIASES.put("평택", "경기");
        MAJOR_CITY_ALIASES.put("화성", "경기");
        MAJOR_CITY_ALIASES.put("남양주", "경기");
        MAJOR_CITY_ALIASES.put("파주", "경기");
        MAJOR_CITY_ALIASES.put("의정부", "경기");
        MAJOR_CITY_ALIASES.put("시흥", "경기");
        MAJOR_CITY_ALIASES.put("김포", "경기");
        MAJOR_CITY_ALIASES.put("광주", "경기"); // 경기도 광주
        MAJOR_CITY_ALIASES.put("군포", "경기");
        MAJOR_CITY_ALIASES.put("하남", "경기");
        MAJOR_CITY_ALIASES.put("오산", "경기");
        MAJOR_CITY_ALIASES.put("이천", "경기");
        MAJOR_CITY_ALIASES.put("안성", "경기");
        MAJOR_CITY_ALIASES.put("구리", "경기");
        MAJOR_CITY_ALIASES.put("포천", "경기");
        MAJOR_CITY_ALIASES.put("양주", "경기");
        MAJOR_CITY_ALIASES.put("동두천", "경기");
        MAJOR_CITY_ALIASES.put("과천", "경기");
        MAJOR_CITY_ALIASES.put("의왕", "경기");
        MAJOR_CITY_ALIASES.put("가평", "경기");
        MAJOR_CITY_ALIASES.put("양평", "경기");
        MAJOR_CITY_ALIASES.put("연천", "경기");
        MAJOR_CITY_ALIASES.put("여주", "경기");
        MAJOR_CITY_ALIASES.put("강화", "인천"); // 강화군은 인천 소속
        MAJOR_CITY_ALIASES.put("옹진", "인천"); // 옹진군은 인천 소속

        MAJOR_CITY_ALIASES.put("부산", "부산");
        MAJOR_CITY_ALIASES.put("대구", "대구");
        MAJOR_CITY_ALIASES.put("광주", "광주"); // 전라도 광주
        MAJOR_CITY_ALIASES.put("대전", "대전");
        MAJOR_CITY_ALIASES.put("울산", "울산");
        MAJOR_CITY_ALIASES.put("세종", "세종");
        MAJOR_CITY_ALIASES.put("춘천", "강원");
        MAJOR_CITY_ALIASES.put("강릉", "강원");
        MAJOR_CITY_ALIASES.put("원주", "강원");
        MAJOR_CITY_ALIASES.put("동해", "강원");
        MAJOR_CITY_ALIASES.put("속초", "강원");
        MAJOR_CITY_ALIASES.put("삼척", "강원");
        MAJOR_CITY_ALIASES.put("태백", "강원");
        MAJOR_CITY_ALIASES.put("제천", "충북");
        MAJOR_CITY_ALIASES.put("청주", "충북");
        MAJOR_CITY_ALIASES.put("충주", "충북");
        MAJOR_CITY_ALIASES.put("천안", "충남");
        MAJOR_CITY_ALIASES.put("공주", "충남");
        MAJOR_CITY_ALIASES.put("보령", "충남");
        MAJOR_CITY_ALIASES.put("아산", "충남");
        MAJOR_CITY_ALIASES.put("서산", "충남");
        MAJOR_CITY_ALIASES.put("논산", "충남");
        MAJOR_CITY_ALIASES.put("당진", "충남");
        MAJOR_CITY_ALIASES.put("계룡", "충남");
        MAJOR_CITY_ALIASES.put("부여", "충남");
        MAJOR_CITY_ALIASES.put("서천", "충남");
        MAJOR_CITY_ALIASES.put("금산", "충남");
        MAJOR_CITY_ALIASES.put("청양", "충남");
        MAJOR_CITY_ALIASES.put("홍성", "충남");
        MAJOR_CITY_ALIASES.put("예산", "충남");
        MAJOR_CITY_ALIASES.put("태안", "충남");
        MAJOR_CITY_ALIASES.put("전주", "전북");
        MAJOR_CITY_ALIASES.put("군산", "전북");
        MAJOR_CITY_ALIASES.put("익산", "전북");
        MAJOR_CITY_ALIASES.put("정읍", "전북");
        MAJOR_CITY_ALIASES.put("남원", "전북");
        MAJOR_CITY_ALIASES.put("김제", "전북");
        MAJOR_CITY_ALIASES.put("목포", "전남");
        MAJOR_CITY_ALIASES.put("여수", "전남");
        MAJOR_CITY_ALIASES.put("순천", "전남");
        MAJOR_CITY_ALIASES.put("나주", "전남");
        MAJOR_CITY_ALIASES.put("광양", "전남");
        MAJOR_CITY_ALIASES.put("경주", "경북");
        MAJOR_CITY_ALIASES.put("포항", "경북");
        MAJOR_CITY_ALIASES.put("김천", "경북");
        MAJOR_CITY_ALIASES.put("안동", "경북");
        MAJOR_CITY_ALIASES.put("구미", "경북");
        MAJOR_CITY_ALIASES.put("영주", "경북");
        MAJOR_CITY_ALIASES.put("영천", "경북");
        MAJOR_CITY_ALIASES.put("상주", "경북");
        MAJOR_CITY_ALIASES.put("문경", "경북");
        MAJOR_CITY_ALIASES.put("경산", "경북");
        MAJOR_CITY_ALIASES.put("울릉", "경북");
        MAJOR_CITY_ALIASES.put("울진", "경북");
        MAJOR_CITY_ALIASES.put("청송", "경북");
        MAJOR_CITY_ALIASES.put("영덕", "경북");
        MAJOR_CITY_ALIASES.put("칠곡", "경북");
        MAJOR_CITY_ALIASES.put("창원", "경남");
        MAJOR_CITY_ALIASES.put("진주", "경남");
        MAJOR_CITY_ALIASES.put("통영", "경남");
        MAJOR_CITY_ALIASES.put("사천", "경남");
        MAJOR_CITY_ALIASES.put("김해", "경남");
        MAJOR_CITY_ALIASES.put("밀양", "경남");
        MAJOR_CITY_ALIASES.put("거제", "경남");
        MAJOR_CITY_ALIASES.put("양산", "경남");

        loadStationMaps();
    }

    // 파일에서 지점 및 측정소 정보를 로드하는 메서드
    private static void loadStationMaps() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 기상청 지점코드 로드
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("seoul/its/info/services/llm/weather/api/kma_station_codes.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("   ") && !line.contains("STN_ID")) { // 데이터 라인만 파싱
                    try {
                        String[] parts = line.trim().split("\s+");
                        if (parts.length > 11) { // 충분한 데이터가 있는지 확인
                            String stnId = parts[0];
                            String stnKo = parts[10]; // 지점명(한글)
                            STN_ID_MAP.put(stnKo, stnId);
                            // 시/도 단위 매핑 추가 (예: 서울 -> 108)
                            if (stnKo.contains("서울")) STN_ID_MAP.put("서울", stnId);
                            else if (stnKo.contains("인천")) STN_ID_MAP.put("인천", stnId);
                            else if (stnKo.contains("부산")) STN_ID_MAP.put("부산", stnId);
                            else if (stnKo.contains("대구")) STN_ID_MAP.put("대구", stnId);
                            else if (stnKo.contains("광주")) STN_ID_MAP.put("광주", stnId);
                            else if (stnKo.contains("대전")) STN_ID_MAP.put("대전", stnId);
                            else if (stnKo.contains("울산")) STN_ID_MAP.put("울산", stnId);
                            else if (stnKo.contains("세종")) STN_ID_MAP.put("세종", stnId);
                            else if (stnKo.contains("강원") || stnKo.contains("춘천") || stnKo.contains("강릉") || stnKo.contains("원주")) STN_ID_MAP.put("강원", stnId);
                            else if (stnKo.contains("충북") || stnKo.contains("청주") || stnKo.contains("충주")) STN_ID_MAP.put("충북", stnId);
                            else if (stnKo.contains("충남") || stnKo.contains("천안") || stnKo.contains("서산")) STN_ID_MAP.put("충남", stnId);
                            else if (stnKo.contains("전북") || stnKo.contains("전주") || stnKo.contains("군산")) STN_ID_MAP.put("전북", stnId);
                            else if (stnKo.contains("전남") || stnKo.contains("목포") || stnKo.contains("여수")) STN_ID_MAP.put("전남", stnId);
                            else if (stnKo.contains("경북") || stnKo.contains("포항") || stnKo.contains("안동")) STN_ID_MAP.put("경북", stnId);
                            else if (stnKo.contains("경남") || stnKo.contains("창원") || stnKo.contains("진주")) STN_ID_MAP.put("경남", stnId);
                            else if (stnKo.contains("제주")) STN_ID_MAP.put("제주", stnId);
                        }
                    } catch (Exception e) {
                        System.err.println("기상청 지점코드 파싱 오류: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("기상청 지점코드 파일을 읽는 중 오류 발생: " + e.getMessage());
        }

        // 에어코리아 측정소 목록 로드
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("seoul/its/info/services/llm/weather/api/airkorea_station_list.json").getInputStream(), StandardCharsets.UTF_8))) {
            JsonNode rootNode = objectMapper.readTree(reader);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items");
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String stationName = item.path("stationName").asText();
                    String addr = item.path("addr").asText();
                    STATION_NAME_MAP.put(stationName, stationName); // 측정소 이름 자체를 매핑
                    // 주소에서 시/도, 구/군 추출하여 매핑 추가
                    String[] addrParts = addr.split(" ");
                    if (addrParts.length > 1) {
                        String cityOrProvince = addrParts[0]; // 시/도
                        String districtOrCity = addrParts[1]; // 구/군 또는 시

                        STATION_NAME_MAP.putIfAbsent(cityOrProvince, stationName);
                        STATION_NAME_MAP.putIfAbsent(cityOrProvince + " " + districtOrCity, stationName);
                        STATION_NAME_MAP.putIfAbsent(districtOrCity, stationName); // 구/군 이름만으로도 매핑
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("에어코리아 측정소 목록 파일을 읽는 중 오류 발생: " + e.getMessage());
        }
    }

    public WeatherService(KmaUltraSrtNcstApiCaller kmaUltraSrtNcstApiCaller,
                          KmaVilageFcstApiCaller kmaVilageFcstApiCaller,
                          KmaMidFcstApiCaller kmaMidFcstApiCaller,
                          AirkoreaStationMesureApiCaller airkoreaStationMesureApiCaller,
                          GeoService geoService) {
        this.kmaUltraSrtNcstApiCaller = kmaUltraSrtNcstApiCaller;
        this.kmaVilageFcstApiCaller = kmaVilageFcstApiCaller;
        this.kmaMidFcstApiCaller = kmaMidFcstApiCaller;
        this.airkoreaStationMesureApiCaller = airkoreaStationMesureApiCaller;
        this.geoService = geoService;
    }

    public WeatherInfo getProcessedCurrentWeather(String city) {
        String baseCity = getMajorCityFromInput(city); // 주요 도시명 추출

        // 1. 도시 이름을 XY 좌표로 변환 (GeoService 사용)
        Map<String, Integer> coords = geoService.getCoordinatesForCity(baseCity);
        if (coords == null || !coords.containsKey("nx") || !coords.containsKey("ny")) {
            throw new IllegalArgumentException("요청하신 도시의 좌표를 찾을 수 없습니다: " + baseCity);
        }
        int nx = coords.get("nx");
        int ny = coords.get("ny");

        // 2. 기상청 초단기실황 API 호출
        KmaUltraSrtNcstResponse kmaResponse = kmaUltraSrtNcstApiCaller.getUltraSrtNcst(nx, ny);

        // 3. API 응답 데이터 가공
        WeatherInfo.WeatherInfoBuilder weatherInfoBuilder = processKmaUltraSrtNcstData(baseCity, kmaResponse).toBuilder();

        // 4. 단기 예보 정보 추가
        String vilageForecast = getProcessedVilageForecast(baseCity);
        weatherInfoBuilder.vilageForecastDescription(vilageForecast);

        // 5. 중기 예보 정보 추가
        String midTermForecast = getProcessedMidTermForecast(baseCity);
        weatherInfoBuilder.midTermForecastDescription(midTermForecast);

        // 6. 미세먼지 정보 추가
        // STN_ID_MAP 대신 STATION_NAME_MAP을 사용하고, 기상청 지점 코드가 아닌 측정소 이름을 사용
        String stationName = STATION_NAME_MAP.getOrDefault(baseCity, STATION_NAME_MAP.getOrDefault("서울", "종로")); // 기본값으로 서울 종로 측정소 설정
        AirkoreaStationMesureResponse airkoreaResponse = airkoreaStationMesureApiCaller.getStationMesure(stationName);
        processAirkoreaDustData(weatherInfoBuilder, airkoreaResponse);

        return weatherInfoBuilder.build();
    }

    private WeatherInfo processKmaUltraSrtNcstData(String city, KmaUltraSrtNcstResponse response) {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null || response.getBody().getItems().isEmpty()) {
            return WeatherInfo.builder()
                    .city(city)
                    .currentWeatherDescription("현재 날씨 정보를 가져올 수 없습니다.")
                    .build();
        }

        List<KmaUltraSrtNcstItem> items = response.getBody().getItems();
        Map<String, String> weatherData = new ConcurrentHashMap<>();

        for (KmaUltraSrtNcstItem item : items) {
            weatherData.put(item.getCategory(), String.valueOf(item.getObsrValue()));
        }

        double temperature = Optional.ofNullable(weatherData.get("T1H"))
                .map(Double::parseDouble)
                .orElse(Double.NaN);
        String skyCondition = Optional.ofNullable(weatherData.get("SKY"))
                .map(SKY_CODE_MAP::get)
                .orElse("알 수 없음");
        String precipitationType = Optional.ofNullable(weatherData.get("PTY"))
                .map(PTY_CODE_MAP::get)
                .orElse("알 수 없음");
        double windSpeed = Optional.ofNullable(weatherData.get("WSD"))
                .map(Double::parseDouble)
                .orElse(Double.NaN);

        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("현재 ").append(city).append("의 날씨는");

        if (!Double.isNaN(temperature)) {
            descriptionBuilder.append(" 기온 ").append(String.format("%.1f도", temperature)).append("입니다.");
        }
        if (!"알 수 없음".equals(skyCondition)) {
            descriptionBuilder.append(" 하늘은 ").append(skyCondition).append("입니다.");
        }
        if (!"알 수 없음".equals(precipitationType) && !"없음".equals(precipitationType)) {
            descriptionBuilder.append(" 현재 ").append(precipitationType).append("이/가 내리고 있습니다.");
        }
        if (!Double.isNaN(windSpeed)) {
            descriptionBuilder.append(" 바람은 초속 ").append(String.format("%.1f m/s", windSpeed)).append("입니다.");
        }

        return WeatherInfo.builder()
                .city(city)
                .currentWeatherDescription(descriptionBuilder.toString().trim())
                .temperature(temperature)
                .skyCondition(skyCondition)
                .precipitationType(precipitationType)
                .windSpeed(windSpeed)
                .build();
    }

    private String getProcessedVilageForecast(String city) {
        Map<String, Integer> coords = geoService.getCoordinatesForCity(city);
        if (coords == null || !coords.containsKey("nx") || !coords.containsKey("ny")) {
            throw new IllegalArgumentException("요청하신 도시의 좌표를 찾을 수 없습니다: " + city);
        }
        int nx = coords.get("nx");
        int ny = coords.get("ny");

        KmaVilageFcstResponse kmaResponse = kmaVilageFcstApiCaller.getVilageFcst(nx, ny);

        return processKmaVilageFcstData(city, kmaResponse);
    }

    private String processKmaVilageFcstData(String city, KmaVilageFcstResponse response) {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null || response.getBody().getItems().isEmpty()) {
            return "단기 예보 정보를 가져올 수 없습니다.";
        }

        List<KmaVilageFcstItem> items = response.getBody().getItems();
        StringBuilder forecastBuilder = new StringBuilder();
        forecastBuilder.append(city).append("의 단기 예보입니다:\n");

        Map<String, Map<String, String>> dailyForecasts = new HashMap<>();

        for (KmaVilageFcstItem item : items) {
            String fcstDate = item.getFcstDate();
            String fcstTime = item.getFcstTime();
            String category = item.getCategory();
            String fcstValue = item.getFcstValue();

            dailyForecasts.computeIfAbsent(fcstDate, k -> new HashMap<>())
                    .put(category + "_" + fcstTime, fcstValue);
        }

        for (Map.Entry<String, Map<String, String>> entry : dailyForecasts.entrySet()) {
            String date = entry.getKey();
            Map<String, String> data = entry.getValue();

            forecastBuilder.append(formatDate(date)).append("\n");

            String[] times = {"0600", "1200", "1800"};
            for (String time : times) {
                String temp = data.getOrDefault("TMP_" + time, "-");
                String sky = SKY_CODE_MAP.getOrDefault(data.getOrDefault("SKY_" + time, ""), "알 수 없음");
                String pty = PTY_CODE_MAP.getOrDefault(data.getOrDefault("PTY_" + time, ""), "없음");

                if (!temp.equals("-") || !sky.equals("알 수 없음") || !pty.equals("없음")) {
                    forecastBuilder.append(time.substring(0, 2)).append("시: 기온 ").append(temp).append("도, 하늘 ").append(sky);
                    if (!pty.equals("없음")) {
                        forecastBuilder.append(", 강수 형태: ").append(pty);
                    }
                    forecastBuilder.append("\n");
                }
            }
        }

        return forecastBuilder.toString().trim();
    }

    private String getProcessedMidTermForecast(String city) {
        // 중기예보 API는 stnId를 사용하므로, 도시 이름으로 stnId를 찾음
        String stnId = STN_ID_MAP.getOrDefault(city, "108"); // 기본값: 전국 (108)

        KmaMidFcstResponse kmaResponse = kmaMidFcstApiCaller.getMidFcst(stnId);

        return processKmaMidFcstData(city, kmaResponse);
    }

    private String processKmaMidFcstData(String city, KmaMidFcstResponse response) {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null || response.getBody().getItems().isEmpty()) {
            return "중기 예보 정보를 가져올 수 없습니다.";
        }

        KmaMidFcstItem item = response.getBody().getItems().get(0); // 중기 예보는 보통 하나의 아이템으로 제공
        StringBuilder forecastBuilder = new StringBuilder();
        forecastBuilder.append(city).append("의 중기 예보입니다:\n");

        forecastBuilder.append("기상전망: ").append(item.getWfSv()).append("\n");

        // 날짜별 강수확률 및 날씨 전망 (예시)
        forecastBuilder.append("3일 후 오전: 강수확률 ").append(item.getRnSt3Am()).append("%, 날씨: ").append(item.getWf3Am()).append("\n");
        forecastBuilder.append("3일 후 오후: 강수확률 ").append(item.getRnSt3Pm()).append("%, 날씨: ").append(item.getWf3Pm()).append("\n");
        forecastBuilder.append("4일 후 오전: 강수확률 ").append(item.getRnSt4Am()).append("%, 날씨: ").append(item.getWf4Am()).append("\n");
        forecastBuilder.append("4일 후 오후: 강수확률 ").append(item.getRnSt4Pm()).append("%, 날씨: ").append(item.getWf4Pm()).append("\n");
        forecastBuilder.append("5일 후 오전: 강수확률 ").append(item.getRnSt5Am()).append("%, 날씨: ").append(item.getWf5Am()).append("\n");
        forecastBuilder.append("5일 후 오후: 강수확률 ").append(item.getRnSt5Pm()).append("%, 날씨: ").append(item.getWf5Pm()).append("\n");
        forecastBuilder.append("6일 후 오전: 강수확률 ").append(item.getRnSt6Am()).append("%, 날씨: ").append(item.getWf6Am()).append("\n");
        forecastBuilder.append("6일 후 오후: 강수확률 ").append(item.getRnSt6Pm()).append("%, 날씨: ").append(item.getWf6Pm()).append("\n");
        forecastBuilder.append("7일 후 오전: 강수확률 ").append(item.getRnSt7Am()).append("%, 날씨: ").append(item.getWf7Am()).append("\n");
        forecastBuilder.append("7일 후 오후: 강수확률 ").append(item.getRnSt7Pm()).append("%, 날씨: ").append(item.getWf7Pm()).append("\n");
        forecastBuilder.append("8일 후: 강수확률 ").append(item.getRnSt8()).append("%, 날씨: ").append(item.getWf8()).append("\n");
        forecastBuilder.append("9일 후: 강수확률 ").append(item.getRnSt9()).append("%, 날씨: ").append(item.getWf9()).append("\n");
        forecastBuilder.append("10일 후: 강수확률 ").append(item.getRnSt10()).append("%, 날씨: ").append(item.getWf10()).append("\n");


        return forecastBuilder.toString().trim();
    }

    private void processAirkoreaDustData(WeatherInfo.WeatherInfoBuilder builder, AirkoreaStationMesureResponse response) {
        if (response != null && response.getBody() != null && response.getBody().getItems() != null && !response.getBody().getItems().isEmpty()) {
            AirkoreaStationMesureItem item = response.getBody().getItems().get(0);
            builder.pm10Value(item.getPm10Value());
            builder.pm10Grade(item.getPm10Grade());
            builder.pm25Value(item.getPm25Value());
            builder.pm25Grade(item.getPm25Grade());
        }
    }

    private String formatDate(String date) {
        return date.substring(4, 6) + "월 " + date.substring(6, 8) + "일";
    }

    private String getMajorCityFromInput(String city) {
        String baseCity = MAJOR_CITY_ALIASES.getOrDefault(city, city);
        return baseCity;
    }

}