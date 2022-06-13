package kz.capitalpay.server.files.repository;

import kz.capitalpay.server.files.model.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
}
