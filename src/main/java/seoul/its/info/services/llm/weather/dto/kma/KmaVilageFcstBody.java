package seoul.its.info.services.llm.weather.dto.kma;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KmaVilageFcstBody {
    private String dataType;
    private List<KmaVilageFcstItem> items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;
} 