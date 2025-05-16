package seoul.its.info.common.security.virustotal;

public enum VirusTotalRiskLevel {
    SAFE(0), // 안전 (Malicious <= 1)
    SUSPICIOUS(1), // 의심 (2 <= Malicious <= 5)
    DANGER(2), // 위험 (Malicious >= 6)
    UNKNOWN(-1); // 알 수 없음 (예: 검사 실패, 관리자 업로드 등)

    private final int level;

    VirusTotalRiskLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}