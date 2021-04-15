package kz.capitalpay.server.currency.repository;

import kz.capitalpay.server.currency.model.SystemCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<SystemCurrency, String> {
}
