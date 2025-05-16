package seoul.its.info.common.util.file.upload.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import seoul.its.info.common.security.virustotal.VirusTotalRiskLevel;

@Data
@RequiredArgsConstructor
public class FileUploadResult {
    private final String relativePath;
    private final String originalFilename;
    private final VirusTotalRiskLevel riskLevel;
    private final String message;
}