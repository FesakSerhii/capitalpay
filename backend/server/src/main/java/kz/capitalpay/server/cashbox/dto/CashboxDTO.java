package kz.capitalpay.server.cashbox.dto;

import kz.capitalpay.server.cashbox.model.Cashbox;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CashboxDTO extends Cashbox {

    Map<String, BigDecimal> balance = new HashMap<>();

    public Map<String, BigDecimal> getBalance() {
        return balance;
    }

    public void setBalance(Map<String, BigDecimal> balance) {
        this.balance = balance;
    }
}
