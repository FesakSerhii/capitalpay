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
@RequestMapping("/api/v1/auth/file")
public class FileUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    Gson gson;

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping(value = "/upload")
    ResultDTO uploadFile(Principal principal, @RequestParam MultipartFile multipartFile) {
        LOGGER.info("\n\n\n I'm here ");
        LOGGER.info(" original name " + multipartFile.getOriginalFilename());
        return fileStorageService.uploadFile(multipartFile, principal);
    }
}
