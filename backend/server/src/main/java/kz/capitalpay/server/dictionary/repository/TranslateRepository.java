package kz.capitalpay.server.dictionary.repository;

import kz.capitalpay.server.dictionary.model.Translate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranslateRepository extends JpaRepository<Translate, Long> {
    List<Translate> findByPage(String page);

    Translate findTopByPageAndTranskey(String page, String transkey);

    Translate findTopByPageAndTranskeyAndLang(String page, String transkey, String lang);
}
