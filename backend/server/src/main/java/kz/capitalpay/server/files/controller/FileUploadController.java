package kz.capitalpay.server.files.controller;


import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.files.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/file")
public class FileUploadController {

    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    Gson gson;

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    ResultDTO uploadFile(Principal principal, @RequestParam MultipartFile multipartFile) {
        logger.info(multipartFile.getOriginalFilename());
        return fileStorageService.uploadFile(multipartFile,principal);
    }
}
