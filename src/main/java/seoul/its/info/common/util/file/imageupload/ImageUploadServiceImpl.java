package seoul.its.info.common.util.file.imageupload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import seoul.its.info.common.util.file.imageupload.dto.ImageDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Value("${app.image.upload-dir}")
    private String uploadDir;

    @Value("${common.file.absolutely-disallowed-extensions}")
    private String disallowedExtensionsStr;

    private final ImageMapper imageMapper;

    public ImageUploadServiceImpl(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public String uploadImage(MultipartFile file, int uploadFrom, Long parentId, Long userId, String loginId, String uploaderIpAddress) {
        if (file.isEmpty()) {
            throw new RuntimeException("업로드할 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("파일 이름이 유효하지 않습니다.");
        }

        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        } else {
            // 확장자가 없는 경우, 필요에 따라 기본 확장자를 설정하거나 오류를 발생시킬 수 있음.
        }

        List<String> disallowedExtensions = Arrays.asList(disallowedExtensionsStr.split(","));
        if (disallowedExtensions.contains(fileExtension)) {
            throw new RuntimeException("허용되지 않는 파일 확장자입니다: " + fileExtension);
        }

        String serviceSubDir = getServiceSubdirectory(uploadFrom);
        if (serviceSubDir == null) {
             throw new RuntimeException("알 수 없는 서비스 분류입니다: " + uploadFrom);
        }

        Path uploadPath;
        if (parentId != null && parentId != 0) {
            uploadPath = Paths.get(uploadDir, serviceSubDir, String.valueOf(parentId));
        } else {
            uploadPath = Paths.get(uploadDir, serviceSubDir);
        }

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }

        String savedFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = uploadPath.resolve(savedFileName);

        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        ImageDto imageDto = new ImageDto();

        if (uploadFrom == 0) {
             if (parentId != null && parentId != 0) {
                 imageDto.setPost_id(parentId);
             } else {
                 imageDto.setPost_id(null);
             }
        }

        imageDto.setUpload_from(uploadFrom);
        imageDto.setUser_id(userId);
        imageDto.setLogin_id(loginId);
        imageDto.setOriginal_name(originalFilename);
        imageDto.setSaved_name(savedFileName);
        imageDto.setIs_image(1);

        String webAccessFilePath;
        if (parentId != null && parentId != 0) {
            webAccessFilePath = serviceSubDir + "/" + parentId + "/" + savedFileName;
        } else {
            webAccessFilePath = serviceSubDir + "/" + savedFileName;
        }
        imageDto.setFile_path(webAccessFilePath);

        imageDto.setFile_size(String.valueOf(file.getSize()));
        imageDto.setFile_type(fileExtension);
        imageDto.setRisk_level(0);
        imageDto.setUploader_ip_address(uploaderIpAddress);
        imageDto.setUpload_by_anonymous(userId == null ? 1 : 0);
        imageDto.setCreated_at(LocalDateTime.now());

        imageMapper.saveFileMetadata(imageDto);

        return "/uploads/" + imageDto.getFile_path();
    }

    @Override
    @Transactional
    public void updateImageParentId(Long temporaryParentId, Long finalParentId) {
        // 이 메서드는 현재 로직에서 직접 사용되지 않을 수 있음.
    }

    @Override
    @Transactional
    public void assignPostIdToTemporaryImages(Long userId, int uploadFrom, Long boardId, Long finalPostId) {
        List<ImageDto> temporaryImages = imageMapper.findTemporaryFiles(userId, uploadFrom);

        if (temporaryImages == null || temporaryImages.isEmpty()) {
            return;
        }

        for (ImageDto image : temporaryImages) {
            if (image.getSaved_name() == null || image.getFile_path() == null) {
                System.err.println("assignPostIdToTemporaryImages - Skipped: image ID " + image.getId() + " has null saved_name or file_path.");
                continue;
            }

            String oldFilePathWithoutBaseDir = image.getFile_path();
            String serviceSubDir = getServiceSubdirectory(uploadFrom);
            String newFileName = image.getSaved_name();

            if (serviceSubDir == null) {
                System.err.println("assignPostIdToTemporaryImages - Skipped: serviceSubDir is null for uploadFrom: " + uploadFrom + ". Image ID: " + image.getId());
                continue;
            }
            if (boardId == null) {
                System.err.println("assignPostIdToTemporaryImages - Skipped: boardId is null. Image ID: " + image.getId());
                continue;
            }

            String newFilePathWithoutBaseDir = serviceSubDir + "/" + boardId + "/" + finalPostId + "/" + newFileName;

            Path sourcePath = Paths.get(uploadDir, oldFilePathWithoutBaseDir);
            Path destinationDir = Paths.get(uploadDir, serviceSubDir, String.valueOf(boardId), String.valueOf(finalPostId));
            Path destinationPath = destinationDir.resolve(newFileName);

            try {
                if (Files.exists(sourcePath)) {
                     Files.createDirectories(destinationDir);
                     Files.move(sourcePath, destinationPath);
                } else {
                     System.err.println("assignPostIdToTemporaryImages - Source file not found for moving: " + sourcePath + " (Image ID: " + image.getId() + ")");
                }
            } catch (IOException e) {
                System.err.println("assignPostIdToTemporaryImages - Error moving file: " + e.getMessage() + " (Source: " + sourcePath + ", Destination: " + destinationPath + ")");
                continue;
            }

            imageMapper.updatePostIdAndPathForFile(image.getId(), finalPostId, newFilePathWithoutBaseDir);
        }
    }

    private String getServiceSubdirectory(int uploadFrom) {
        switch (uploadFrom) {
            case 0:
                return "boards";
            case 1:
                return "comments";
            default:
                return null;
        }
    }
} 