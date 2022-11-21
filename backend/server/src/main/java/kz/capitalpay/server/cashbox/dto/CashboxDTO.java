package kz.capitalpay.server.cashbox.dto;

import kz.capitalpay.server.cashbox.model.Cashbox;

import java.util.List;


public class CashboxDTO extends Cashbox {

    private List<CashboxBalanceDTO> incomingBalance;
    private List<CashboxBalanceDTO> outgoingBalance;

    public List<CashboxBalanceDTO> getIncomingBalance() {
        return incomingBalance;
    }

    public void setIncomingBalance(List<CashboxBalanceDTO> incomingBalance) {
        this.incomingBalance = incomingBalance;
    }

    public List<CashboxBalanceDTO> getOutgoingBalance() {
        return outgoingBalance;
    }

    public void setOutgoingBalance(List<CashboxBalanceDTO> outgoingBalance) {
        this.outgoingBalance = outgoingBalance;
    }
}
