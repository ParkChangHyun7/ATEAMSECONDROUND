package seoul.its.info.services.contact;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import seoul.its.info.common.util.file.upload.FileUploadService;
import seoul.its.info.common.util.file.upload.dto.FileUploadResult;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.common.util.file.upload.dto.FileUploadRequest;
import seoul.its.info.common.util.ClientIpGetHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactPageServiceImpl implements ContactPageService {

    private final ContactPageMapper contactMapper;
    private final FileUploadService fileUploadService;
    private final ClientIpGetHelper clientIpGetHelper;

    @Value("${contact.file.max-size:10MB}")
    private String maxFileSizeValue;

    @Value("${contact.file.allowed-extensions}")
    private String allowedExtensionsValue; // 프로퍼티에서 읽어올 문자열

    @Value("${common.file.app-temp-upload-dir:./app-temp-uploads}")
    private String appTempUploadDir;

    private static final int ADMIN_ROLE_THRESHOLD = 100;
    private static final int CONTACT_UPLOAD_FROM = 2;

    @Override
    @Transactional
    public void saveContactInquiry(ContactRequestDto contactDto, HttpServletRequest httpRequest)
            throws IOException {
        // 1. 문의 내용을 먼저 저장하여 ID 확보
        if (contactDto.getAttachment() != null && !contactDto.getAttachment().isEmpty()) {
            contactMapper.insertContactInquiryWithAttachment(contactDto);
        } else {
            contactMapper.insertContactInquiry(contactDto);
        }
        log.info("문의 내용 DB 저장 완료: Name={}, Subject={}, ContactId={}", contactDto.getName(), contactDto.getSubject(),
                contactDto.getId());

        // 2. 첨부파일이 있을 경우 파일 임시 저장 및 업로드 서비스 호출
        MultipartFile attachment = contactDto.getAttachment();
        if (attachment != null && !attachment.isEmpty()) {
            String originalFilename = attachment.getOriginalFilename(); // 원본 파일명은 로깅 등을 위해 유지
            try {
                int userRole = 0;
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof UserDetailsImpl) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                    if (userDetails.getRole() != null) {
                        userRole = userDetails.getRole();
                    }
                }

                boolean isAdmin = (userRole >= ADMIN_ROLE_THRESHOLD);

                long maxSizeBytes;
                Set<String> allowedExtensions;

                if (isAdmin) {
                    log.info("관리자 권한으로 파일 제한 없이 업로드 진행: Role={}", userRole);
                    maxSizeBytes = Long.MAX_VALUE;
                    allowedExtensions = null;
                } else {
                    maxSizeBytes = DataSize.parse(maxFileSizeValue).toBytes();
                    // 프로퍼티 값을 Set으로 변환
                    allowedExtensions = parseAllowedExtensions(allowedExtensionsValue);
                }

                log.info("문의 첨부파일 공통 처리 서비스 호출 준비 - FileName={}, Role={}, IsAdmin={}",
                        originalFilename, userRole, isAdmin);

                FileUploadRequest fileUploadRequest = new FileUploadRequest();
                fileUploadRequest.setUploadContext("contact");
                fileUploadRequest.setAllowedExtensions(allowedExtensions);
                fileUploadRequest.setMaxSizeBytes(maxSizeBytes);
                fileUploadRequest.setRole(userRole);
                fileUploadRequest.setUploadFrom(CONTACT_UPLOAD_FROM);
                fileUploadRequest.setServiceId(contactDto.getId()); // 문의 ID를 serviceId에 설정
                fileUploadRequest.setClientIpAddress(clientIpGetHelper.getClientIpAddress(httpRequest));

                CompletableFuture<FileUploadResult> futureUploadResult = fileUploadService
                        .uploadFile(attachment, fileUploadRequest);

                // 비동기 작업 완료 후 로그 출력 (임시 파일 삭제 로직 제거)
                futureUploadResult.thenAccept(uploadResult -> {
                    log.info("문의 첨부파일 비동기 처리 완료: Path={}, RiskLevel={}", uploadResult.getRelativePath(),
                            uploadResult.getRiskLevel());
                }).exceptionally(e -> {
                    log.error("문의 첨부파일 비동기 처리 중 오류 발생", e);
                    // TODO: 비동기 예외 처리 방안 수립 (예: 실패 알림, 재시도 로직 등)
                    return null; // exceptionally는 결과를 반환해야 하므로 null 반환
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("파일 처리 중 인터럽트 발생: {}", originalFilename, e);
                throw new IOException("파일 처리 중 오류가 발생했습니다 (Interrupted)", e);
            } catch (IOException e) {
                log.error("파일 처리 중 IOException 발생: {}", originalFilename, e);
                throw e;
            } catch (Exception e) {
                log.error("파일 처리 중 예상치 못한 오류 발생: {}", originalFilename, e);
                throw new IOException("파일 처리 중 예상치 못한 오류가 발생했습니다.", e);
            }
        }
    }

    private Set<String> parseAllowedExtensions(String extensionsString) {
        if (!StringUtils.hasText(extensionsString)) {
            log.warn("'contact.file.allowed-extensions' 속성이 비어있거나 설정되지 않았습니다. 모든 파일 형식이 허용될 수 있습니다(위험). ");
            return Collections.emptySet(); // 또는 null을 반환하여 FileUploadService의 기본 정책 따르기
        }
        // 콤마로 구분하고, 공백 제거, 소문자로 변환하여 Set 생성
        return Arrays.stream(extensionsString.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}