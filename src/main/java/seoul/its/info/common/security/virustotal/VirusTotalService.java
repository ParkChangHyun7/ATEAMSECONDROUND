package seoul.its.info.common.security.virustotal;

import java.io.IOException;
import java.io.InputStream;

public interface VirusTotalService {

    VirusTotalScanResult scanFile(InputStream inputStream) throws IOException, InterruptedException;

}