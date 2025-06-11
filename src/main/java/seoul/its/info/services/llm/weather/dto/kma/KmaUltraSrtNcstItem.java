package seoul.its.info.services.llm.weather.dto.kma;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KmaUltraSrtNcstItem {
    private String baseDate;
    private String baseTime;
    private String category;
    private double obsrValue;
    private int nx;
    private int ny;
} 