package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import kz.capitalpay.server.paysystems.systems.halyksoap.dto.RegisterPaymentsDateDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykRegisterPaymentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;

@RestController
@RequestMapping("/api/v1/auth/paysystems/register")
public class HalykRegisterPaymentsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HalykRegisterPaymentsController.class);

    private final HalykRegisterPaymentsService halykRegisterPaymentsService;

    public HalykRegisterPaymentsController(HalykRegisterPaymentsService halykRegisterPaymentsService) {
        this.halykRegisterPaymentsService = halykRegisterPaymentsService;
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST,
            produces = "text/plain;charset=UTF-8")
    @RolesAllowed({ADMIN, OPERATOR})
    @ResponseBody
    public ResponseEntity<Object> halykRegisterPaymentsDownload(@RequestBody RegisterPaymentsDateDTO dateDTO)
            throws IOException {
        File file = halykRegisterPaymentsService.createTextFileForDownload(dateDTO);
        LOGGER.info("file exist with name " + file.getName());
        byte[] bytes = Files.readAllBytes(file.toPath());
        ByteArrayResource byteResource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()))
                .header("fileName", file.getName())
                .body(byteResource);
    }
}
