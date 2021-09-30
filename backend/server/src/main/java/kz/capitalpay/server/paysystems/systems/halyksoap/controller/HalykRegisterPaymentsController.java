package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import kz.capitalpay.server.paysystems.systems.halyksoap.dto.RegisterPaymentsDateDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykRegisterPaymentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;

@RestController
@RequestMapping("/api/v1/paysystems/register")
public class HalykRegisterPaymentsController {
    private final Logger logger = LoggerFactory.getLogger(HalykRegisterPaymentsController.class);

    @Autowired
    private HalykRegisterPaymentsService halykRegisterPaymentsService;

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @RolesAllowed({ADMIN, OPERATOR})
    public ResponseEntity<Object> halykRegisterPaymentsDownload(@RequestBody RegisterPaymentsDateDTO dateDTO)
            throws IOException {
        File file = halykRegisterPaymentsService.createTextFileForDownload(dateDTO);
        logger.info("file exist with name " + file.getName());
        InputStream is = new FileInputStream(file);
        byte[] bytes = is.readAllBytes();
        ByteArrayResource byteResource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()))
                .body(byteResource);
    }
}
