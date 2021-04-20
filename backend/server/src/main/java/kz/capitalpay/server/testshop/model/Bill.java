package kz.capitalpay.server.testshop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Bill {
    @Id
    @GeneratedValue
    Long id;

    BigDecimal amount;


}
