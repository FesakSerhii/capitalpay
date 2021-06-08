package kz.capitalpay.server.registerpayments.controller;

import kz.capitalpay.server.registerpayments.dto.ContributorDTO;
import kz.capitalpay.server.registerpayments.service.RegisterPaymentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/registerpaymnets")
public class RegisterPaymentsController {
    private final Logger logger = LoggerFactory.getLogger(RegisterPaymentsController.class);

    @Autowired
    private RegisterPaymentsService registerPaymentsService;

    @RequestMapping(value = "/download", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> downloadFile(@RequestBody ContributorDTO contributorDTO)
            throws IOException {
        File file = registerPaymentsService.createTextFileForDownload(contributorDTO);
        logger.info("file exist with name" + file.getName());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(
                MediaType.parseMediaType("application/txt")).body(resource);
    }
}
