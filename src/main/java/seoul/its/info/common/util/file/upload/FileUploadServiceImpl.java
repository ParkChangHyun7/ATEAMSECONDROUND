package seoul.its.info.common.util.file.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import seoul.its.info.common.security.virustotal.VirusTotalRiskLevel;
import seoul.its.info.common.security.virustotal.VirusTotalScanResult;
import seoul.its.info.common.security.virustotal.VirusTotalService;
import seoul.its.info.common.util.file.upload.dto.FileUploadRequest;
import seoul.its.info.common.util.file.upload.dto.FileUploadResult;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.common.util.file.upload.dto.FileUploadDto;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    private final VirusTotalService virusTotalService;
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final CommonFileMapper commonFileMapper;

    @Value("${common.file.base-upload-dir:./uploads}")
    private String baseUploadDir;

    @Value("${common.file.absolutely-disallowed-extensions:exe,bat,sh,msi,cmd,com,dll}")
    private String absolutelyDisallowedExtensionsValue;

    @Value("${common.file.app-temp-upload-dir:./app-temp-uploads}")
    private String appTempUploadDir;

    private Set<String> absolutelyDisallowedExtensionsSet;

    private static final int ADMIN_ROLE_THRESHOLD = 100;
    // ANONYMOUS_UPLOAD_FROM (13)은 upload_by_anonymous 플래그로 대체

    @PostConstruct
    public void initialize() {
        if (StringUtils.hasText(absolutelyDisallowedExtensionsValue)) {
            absolutelyDisallowedExtensionsSet = Arrays.stream(absolutelyDisallowedExtensionsValue.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } else {
            absolutelyDisallowedExtensionsSet = Collections.emptySet();
            log.warn("'common.file.absolutely-disallowed-extensions' 속성이 비어있거나 설정되지 않았습니다.");
        }
        log.info("Absolutely disallowed extensions initialized: {}", absolutelyDisallowedExtensionsSet);
    }

    @Override
    @Async("fileUploadTaskExecutor")
    public CompletableFuture<FileUploadResult> uploadFile(MultipartFile multipartFile, FileUploadRequest request) {

        Path appManagedTempFilePath = null;

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            long fileSize = multipartFile.getSize();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);

            String uploadContext = request.getUploadContext();
            Set<String> allowedExtensions = request.getAllowedExtensions();
            long maxSizeBytes = request.getMaxSizeBytes();
            int role = request.getRole();
            int uploadFrom = request.getUploadFrom();
            Long serviceId = request.getServiceId();
            String clientIpAddress = request.getClientIpAddress();
            Integer uploadByAnonymous = request.getUploadByAnonymous();

            if (multipartFile.isEmpty()) {
                throw new IOException("업로드된 파일이 비어있습니다.");
            }
            if (!StringUtils.hasText(originalFilename)) {
                throw new IOException("원본 파일명이 유효하지 않습니다.");
            }
            if (!StringUtils.hasText(uploadContext)) {
                throw new IllegalArgumentException("파일 업로드 컨텍스트(경로)가 지정되지 않았습니다.");
            }

            Path tempUploadDirPath = Paths.get(appTempUploadDir);
            ensureDirectoryExists(tempUploadDirPath);
            String uniqueTempFilename = generateUniqueFilenameBase()
                    + (StringUtils.hasText(fileExtension) ? "." + fileExtension : "");
            appManagedTempFilePath = tempUploadDirPath.resolve(uniqueTempFilename);

            try (var inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, appManagedTempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("애플리케이션 관리 임시 파일 저장 완료: {}", appManagedTempFilePath);

            boolean isAdmin = (role >= ADMIN_ROLE_THRESHOLD);
            log.info(
                    "공통 파일 업로드 처리 시작 (비동기) - Context: {}, OriginalFile: {}, Role: {}, IsAdmin: {}, UploadFrom: {}, ServiceId: {}, Anonymous: {}, IP: {}, AppTempPath: {}",
                    uploadContext, originalFilename, role, isAdmin, uploadFrom, serviceId, uploadByAnonymous,
                    clientIpAddress, appManagedTempFilePath);

            if (!isAdmin) {
                validateFileProperties(fileSize, fileExtension, allowedExtensions, maxSizeBytes);
            }

            String uniqueFilenameBase = generateUniqueFilenameBase();
            String savedFilename = uniqueFilenameBase + (StringUtils.hasText(fileExtension) ? "." + fileExtension : "");

            VirusTotalRiskLevel riskLevel;
            Object analysisReportForJson = null;
            String message = null;

            if (isAdmin || (uploadByAnonymous != null && uploadByAnonymous == 1)) {
                log.info("관리자 또는 익명 권한으로 VirusTotal 검사 건너뛰기: {}", savedFilename);
                riskLevel = VirusTotalRiskLevel.UNKNOWN;
            } else {
                try {
                    log.info("VirusTotal 검사 시작 (파일: {}): {}", savedFilename, appManagedTempFilePath);
                    try (var tempFileInputStream = Files.newInputStream(appManagedTempFilePath)) {
                        VirusTotalScanResult scanResult = virusTotalService.scanFile(tempFileInputStream);
                        if (scanResult != null) {
                            riskLevel = scanResult.getRiskLevel();
                            analysisReportForJson = scanResult.getAnalysisReport();
                            log.info("VirusTotal 검사 완료: RiskLevel={}", riskLevel);
                        } else {
                            log.warn("VirusTotal 검사 결과가 null입니다. UNKNOWN으로 처리합니다. File: {}", savedFilename);
                            riskLevel = VirusTotalRiskLevel.UNKNOWN;
                            message = "파일 검증에 실패했습니다. 잠시 후 다시 시도해주세요.";
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    log.error("VirusTotal 검사 중 오류 발생. UNKNOWN으로 처리합니다. File: {}, Error: {}", savedFilename,
                            e.getMessage());
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    riskLevel = VirusTotalRiskLevel.UNKNOWN;
                    message = "파일 검증 중 오류가 발생했습니다.";
                }
            }

            String subDirectory = determineSubDirectory(riskLevel);
            Path contextUploadDir = Paths.get(baseUploadDir, uploadContext);
            Path targetDirectory = contextUploadDir.resolve(subDirectory);
            ensureDirectoryExists(targetDirectory);

            Path finalFilePath = targetDirectory.resolve(savedFilename);

            try (var tempFileInputStream = Files.newInputStream(appManagedTempFilePath)) {
                Files.copy(tempFileInputStream, finalFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("파일 최종 저장 완료: {}", finalFilePath);

            if (analysisReportForJson != null
                    && (riskLevel == VirusTotalRiskLevel.SUSPICIOUS || riskLevel == VirusTotalRiskLevel.DANGER)) {
                saveScanResultAsJson(targetDirectory, uniqueFilenameBase, analysisReportForJson);
            }

            Path relativePath = Paths.get(uploadContext, subDirectory, savedFilename);
            String relativePathString = relativePath.toString().replace("\\\\", "/");

            FileUploadDto fileUploadDto = new FileUploadDto();
            fileUploadDto.setOriginalName(originalFilename);
            fileUploadDto.setSavedName(savedFilename);
            fileUploadDto.setFilePath(relativePathString);
            fileUploadDto.setFileSize(fileSize);
            fileUploadDto.setFileType(fileExtension);
            fileUploadDto.setRisklevel(riskLevel.getLevel());
            fileUploadDto.setUploaderIpAddress(clientIpAddress);
            fileUploadDto.setUploadFrom(uploadFrom);
            fileUploadDto.setCount(0);

            Long userId = null;
            String loginId = null;
            if (uploadByAnonymous != null && uploadByAnonymous == 0) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()
                        && authentication.getPrincipal() instanceof UserDetailsImpl) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    userId = userDetails.getId();
                    loginId = userDetails.getUsername();
                }
            }

            fileUploadDto.setUserId(userId);
            fileUploadDto.setLoginId(loginId);

            fileUploadDto.setIsDeleted((uploadByAnonymous != null && uploadByAnonymous == 1) ? -1 : 0);
            fileUploadDto.setUploadByAnonymous(uploadByAnonymous);

            switch (uploadFrom) {
                case 0:
                    fileUploadDto.setPostId(serviceId);
                    fileUploadDto.setCommentId(null);
                    fileUploadDto.setContactId(null);
                    fileUploadDto.setServiceId(null);
                    break;
                case 1:
                    fileUploadDto.setCommentId(serviceId);
                    fileUploadDto.setPostId(null);
                    fileUploadDto.setContactId(null);
                    fileUploadDto.setServiceId(null);
                    break;
                case 2:
                    fileUploadDto.setContactId(serviceId);
                    fileUploadDto.setPostId(null);
                    fileUploadDto.setCommentId(null);
                    fileUploadDto.setServiceId(null);
                    break;
                default:
                    fileUploadDto.setServiceId(serviceId);
                    fileUploadDto.setPostId(null);
                    fileUploadDto.setCommentId(null);
                    fileUploadDto.setContactId(null);
                    log.warn("알 수 없는 uploadFrom 값: {}. serviceId [{}]를 general serviceId 필드에 매핑합니다.", uploadFrom,
                            serviceId);
                    break;
            }

            try {
                commonFileMapper.insertFile(fileUploadDto);
                log.info("파일 메타데이터 DB 저장 완료: FileId={}, SavedName={}", fileUploadDto.getId(),
                        fileUploadDto.getSavedName());
            } catch (Exception e) {
                log.error("파일 메타데이터 DB 저장 실패: SavedName={}", fileUploadDto.getSavedName(), e);
            }

            log.info("공통 파일 업로드 처리 완료 (비동기) - RelativePath: {}, RiskLevel: {}", relativePathString, riskLevel);

            final Path finalAppManagedTempFilePath = appManagedTempFilePath;
            return CompletableFuture
                    .completedFuture(new FileUploadResult(relativePathString, originalFilename, riskLevel, message))
                    .whenCompleteAsync((result, throwable) -> {
                        if (finalAppManagedTempFilePath != null) {
                            try {
                                Files.deleteIfExists(finalAppManagedTempFilePath);
                                log.info("애플리케이션 관리 임시 파일 삭제 완료: {}", finalAppManagedTempFilePath);
                            } catch (IOException e) {
                                log.error("애플리케이션 관리 임시 파일 삭제 실패: {}", finalAppManagedTempFilePath, e);
                            }
                        }
                        if (throwable != null) {
                            log.error("파일 업로드 비동기 처리 중 최종 단계에서 예외 발생", throwable);
                        }
                    });

        } catch (IOException e) {
            log.error("파일 업로드 초기 처리 중 오류 발생", e);
            if (appManagedTempFilePath != null) {
                try {
                    Files.deleteIfExists(appManagedTempFilePath);
                    log.info("예외 발생으로 인한 애플리케이션 관리 임시 파일 삭제: {}", appManagedTempFilePath);
                } catch (IOException cleanupEx) {
                    log.error("예외 발생 후 임시 파일 삭제 실패: {}", appManagedTempFilePath, cleanupEx);
                }
            }
            return CompletableFuture.failedFuture(e);
        }
    }

    private void validateFileProperties(long fileSize,
            String fileExtension,
            Set<String> allowedExtensions,
            long maxSizeBytes) throws IOException {
        if (fileSize > maxSizeBytes) {
            throw new IOException("파일 크기가 허용된 최대 크기(" + maxSizeBytes + " bytes)를 초과합니다. 크기: " + fileSize);
        }

        if (StringUtils.hasText(fileExtension)) {
            if (absolutelyDisallowedExtensionsSet.contains(fileExtension.toLowerCase())) {
                throw new IOException("업로드가 절대 금지된 파일 형식입니다: " + fileExtension);
            }

            if (allowedExtensions != null && !allowedExtensions.isEmpty() &&
                    !allowedExtensions.contains(fileExtension.toLowerCase())) {
                throw new IOException("해당 서비스에서 허용되지 않는 파일 형식입니다: " + fileExtension);
            }
        }
    }

    private String generateUniqueFilenameBase() {
        return UUID.randomUUID().toString();
    }

    private String determineSubDirectory(VirusTotalRiskLevel riskLevel) {
        switch (riskLevel) {
            case DANGER:
                return "danger";
            case SUSPICIOUS:
                return "suspicious";
            case UNKNOWN:
            case SAFE:
            default:
                return "safe";
        }
    }

    private void ensureDirectoryExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            log.info("업로드 디렉토리 생성: {}", directory.toAbsolutePath());
        }
    }

    private void saveScanResultAsJson(Path directory, String filenameBase, Object report) {
        Path jsonFilePath = directory.resolve(filenameBase + ".json");
        try {
            String jsonResult = objectMapper.writeValueAsString(report);
            Files.writeString(jsonFilePath, jsonResult, StandardCharsets.UTF_8);
            log.info("VirusTotal 분석 결과 JSON 저장 완료: {}", jsonFilePath);
        } catch (IOException e) {
            log.error("VirusTotal JSON 결과 저장 실패: {}", jsonFilePath, e);
        }
    }
}