package kz.capitalpay.server.files.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FileStorage {

    @Id
    @GeneratedValue
    Long id;
    Long authorId;
    String filename;
    String type;
    String hash;
    String path;
    Long size;
    String extension;

    public FileStorage() {
    }

    public FileStorage(Long authorId, String filename, String type, String hash, String path, Long size, String extension) {
        this.id = id;
        this.authorId = authorId;
        this.filename = filename;
        this.type = type;
        this.hash = hash;
        this.path = path;
        this.size = size;
        this.extension = extension;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
