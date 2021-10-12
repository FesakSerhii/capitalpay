package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import kz.capitalpay.server.paysystems.systems.halyksoap.dto.RegisterPaymentsDateDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykRegisterPaymentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;

@RestController
@RequestMapping("/api/v1/paysystems/register")
public class HalykRegisterPaymentsController {
    private final Logger logger = LoggerFactory.getLogger(HalykRegisterPaymentsController.class);

    @Autowired
    private HalykRegisterPaymentsService halykRegisterPaymentsService;

    @RequestMapping(value = "/download", method = RequestMethod.POST,
            produces = "text/plain;charset=UTF-8")
    @RolesAllowed({ADMIN, OPERATOR})
    @ResponseBody
    public void halykRegisterPaymentsDownload(@RequestBody RegisterPaymentsDateDTO dateDTO,
                                              HttpServletResponse response)
            throws IOException {
        File file = halykRegisterPaymentsService.createTextFileForDownload(dateDTO);
        logger.info("file exist with name " + file.getName());
        byte[] bytes = Files.readAllBytes(file.toPath());
        ByteArrayResource byteResource = new ByteArrayResource(bytes);
        response.addHeader("fileName", file.getName());
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        response.setStatus(200);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes, 0, bytes.length);
        } catch (Exception excp) {
            excp.printStackTrace();
        }


//        return ResponseEntity.ok()
//                .contentType(MediaType.TEXT_PLAIN)
//                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()))
//                .header("fileName", file.getName())
//                .body(byteResource);
    }
}
