package kz.capitalpay.server.files.controller;


import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.files.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/file")
public class FileUploadController {

    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    Gson gson;

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping(value= "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResultDTO uploadFile(Principal principal, @RequestParam MultipartFile multipartFile) {

        logger.info(" original name " + multipartFile.getOriginalFilename());
        return fileStorageService.uploadFile(multipartFile,principal);
    }
}
