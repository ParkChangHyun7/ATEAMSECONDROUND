package seoul.its.info.services.llm.weather.dto.kma;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KmaUltraSrtNcstResponse {
    private KmaUltraSrtNcstHeader header;
    private KmaUltraSrtNcstBody body;
} 