package seoul.its.info.common.util.file.upload;

import seoul.its.info.common.util.file.upload.dto.FileUploadRequest;
import seoul.its.info.common.util.file.upload.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface FileUploadService {
    CompletableFuture<FileUploadResult> uploadFile(MultipartFile multipartFile, FileUploadRequest request)
            throws IOException, InterruptedException;
}