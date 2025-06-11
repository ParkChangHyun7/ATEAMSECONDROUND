package seoul.its.info.services.llm.weather.dto.kma;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KmaUltraSrtNcstHeader {
    private String resultCode;
    private String resultMsg;
} 