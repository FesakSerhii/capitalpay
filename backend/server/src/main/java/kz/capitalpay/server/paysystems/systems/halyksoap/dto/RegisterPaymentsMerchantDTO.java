package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RegisterPaymentsMerchantDTO extends RegisterPaymentsCommonMerchantFieldsDTO {
    private String iik;
    private String bik;
    private String iinbin;
    private BigDecimal amount;
    private String bankname;

    public String getIik() {
        return iik;
    }

    public void setIik(String iik) {
        this.iik = iik;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getIinbin() {
        return iinbin;
    }

    public void setIinbin(String iinbin) {
        this.iinbin = iinbin;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    @Override
    public String toString() {
        return bik.trim() + "¤" +
                iik.trim() + "¤¤" +
                iinbin.trim() + "¤" +
                amount.setScale(2, RoundingMode.HALF_DOWN) + "¤" +
                bankname.trim() + "¤" +
                getNaznpl_merch().trim() + "¤      ¤" +
//                getBclassd_merch() + "¤" +
                getKod_merch().trim() + "¤" +
                getKnp_merch().trim() + "¤¤¤\r\n";
//                getKnp_merch() + "¤" +
//                getRnna_merch() + "¤" +
//                getPlatel_merch() + "¤¤¤\r\n";
    }
}
