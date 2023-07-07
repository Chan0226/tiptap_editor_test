package com.example.editor.common;

import com.example.editor.model.AttachmentVo;
import com.example.editor.service.AttachService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${spring.servlet.multipart.location}")
    private String SAVE_LOCATION;
    @Value("${spring.servlet.multipart.maxFileSize}")
    private long MAX_FILE_SIZE;

    @Autowired
    AttachService attachService;

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(MultipartFile uploadfile) {
        log.info("파일:{}", uploadfile);
        UploadDto uploadDto = new UploadDto();
        long fileSize = uploadfile.getSize();

        // 최대 파일 사이즈 넘은 경우
        if (fileSize > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("파일 용량은 10MB 미만이어야 합니다.");
        }

        // 파일 확장자 검사
        String contentType = uploadfile.getContentType();
        // 확장자가 없을경우 => 필요에 따라 확장자 지정으로 수정
        if (contentType == null) {
            return ResponseEntity.badRequest().body("잘못된 파일 형식입니다.");
        }

        // 파일 저장 폴더 경로
        String folderPath = SAVE_LOCATION;
        // 업로드 파일 본래 이름(file.pdf, file.jpg)
        String orgFileName = uploadfile.getOriginalFilename();
        // 파일 확장자(.이후)
        String extension = orgFileName.substring(orgFileName.lastIndexOf("."));
        // 확장자 이전 파일 이름(file)
        String nameWithoutExtension = orgFileName.substring(0, orgFileName.lastIndexOf("."));
        // 파일 이름 + '_' + 1679466528386 + "확장자"
        String fileName = nameWithoutExtension + "_" + System.currentTimeMillis() + extension;
        // 폴더 포함 full 경로
        String fullPath = folderPath + fileName;
        // 폴더 제외 파일 경로
        String filePath =  "/upload/" + fileName;

        try {
            // 파일 저장
            Files.write(Paths.get(fullPath), uploadfile.getBytes());

            // 이미지 파일인 경우와 아닌경우 (나중에 이미지 보여주고 안보여주고 판단하기 위해 사용)
            if (contentType.startsWith("image/")) {
                uploadDto.setFileType("image");
            }else if (contentType.startsWith("video/")) { 
                uploadDto.setFileType("video");
            }
            else {
                uploadDto.setFileType("other");
            }

        } catch (IOException e) {
            // 파일 저장 실패
            return ResponseEntity.badRequest().body("파일 저장에 실패했습니다.: " + e.getMessage());
        }
        // 파일 경로
        uploadDto.setFilePath(filePath);
        return ResponseEntity.ok(uploadDto.getFilePath());
    }

    @PostMapping("/insertAttach")
    public void insertAttach(@RequestBody AttachmentVo attachmentVo) {
        log.info("업로드 파일:{}", attachmentVo);
        attachService.insertAttach(attachmentVo);
    }
}

