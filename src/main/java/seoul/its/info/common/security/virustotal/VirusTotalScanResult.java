package seoul.its.info.common.security.virustotal;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VirusTotalScanResult {
    private final VirusTotalRiskLevel riskLevel;
    private final VirusTotalAnalysisReport analysisReport;
    private final int maliciousCount;
} 