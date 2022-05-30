package kz.capitalpay.server.pages.repository;

import kz.capitalpay.server.pages.model.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaticPageRepository extends JpaRepository<StaticPage, Long> {

    StaticPage findTopByTagAndLanguage(String tag, String language);

    List<StaticPage> findByLanguage(String language);

    List<StaticPage> findByTag(String tag);
}
