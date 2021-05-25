package kz.capitalpay.server.cashbox.dto;

import kz.capitalpay.server.cashbox.model.Cashbox;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CashboxDTO extends Cashbox {

    Map<String, BigDecimal> currencyList = new HashMap<>();

    public Map<String, BigDecimal> getCurrencyList() {

        return currencyList;
    }

    public void setCurrencyList(Map<String, BigDecimal> currencyList) {
        this.currencyList = currencyList;
    }
}
