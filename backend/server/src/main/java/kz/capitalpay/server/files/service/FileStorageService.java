package kz.capitalpay.server.files.service;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.files.model.FileStorage;
import kz.capitalpay.server.files.repository.FileStorageRepository;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileStorageService {

    Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${filestorage.path}")
    String fileStoragePath;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    FileStorageRepository fileStorageRepository;

    public ResultDTO uploadFile(MultipartFile multipartFile, Principal principal) {
        try {
            ApplicationUser user = applicationUserService.getUserByLogin(principal.getName());

            String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String contentType = multipartFile.getContentType();
            Long size = multipartFile.getSize();
            String fileExtension = getExtensionByStringHandling(originalFilename).orElse("");
            Path copyLocation = Paths.get(fileStoragePath + File.separator + originalFilename);
            Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            String shaChecksum = getFileChecksum(shaDigest, copyLocation.toFile());
            Path newName = Paths.get(fileStoragePath + File.separator + shaChecksum + fileExtension);
            FileStorage newFile = new FileStorage(user.getId(), originalFilename, contentType, shaChecksum,
                    shaChecksum + fileExtension, size, fileExtension);
            if (newName.toFile().exists()) {
                logger.info("{} exists", newName.toString());
                Files.delete(copyLocation);
            } else {
                Files.move(copyLocation, copyLocation.resolveSibling(newName));
                newFile = fileStorageRepository.save(newFile);
            }

            return new ResultDTO(true, newFile, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".")));
    }


    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    public List<FileStorage> getFilListById(List<Long> filesIdList) {

        List<FileStorage> result = new ArrayList<>();
        for (Long fileId : filesIdList) {
            FileStorage fileStorage = fileStorageRepository.findById(fileId).orElse(null);
            if (fileStorage != null) {
                result.add(fileStorage);
            }
        }
        return result;
    }
}
