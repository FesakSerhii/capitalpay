package kz.capitalpay.server.pages.repository;

import kz.capitalpay.server.pages.model.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticPageRepository extends JpaRepository<StaticPage,Long> {
}
