package kz.capitalpay.server.cashbox.dto;

import kz.capitalpay.server.cashbox.model.Cashbox;

import java.util.List;


public class CashboxDTO extends Cashbox {

    private List<CashboxBalanceDTO> balance;

    public List<CashboxBalanceDTO> getBalance() {
        return balance;
    }

    public void setBalance(List<CashboxBalanceDTO> balance) {
        this.balance = balance;
    }
}
