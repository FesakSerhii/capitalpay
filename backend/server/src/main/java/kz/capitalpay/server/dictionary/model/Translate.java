package kz.capitalpay.server.dictionary.model;

import javax.persistence.*;

@Entity
public class Translate {
    @Id
    @GeneratedValue
    Long id;
    String page;
    String lang;
    String transkey;
    @Column(length = 16383)
    String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTranskey() {
        return transkey;
    }

    public void setTranskey(String transkey) {
        this.transkey = transkey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
