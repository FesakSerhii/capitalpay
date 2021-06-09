package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

public class RegisterPaymentsMerchantDTO {
    private String iik;
    private String bik;
    private String iinbin;
    private String amount;
    private String bankname;
    private String naznpl_merch;
    private String bclassd_merch;
    private String kod_merch;
    private String knp_merch;
    private String rnna_merch;
    private String platel_merch;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getNaznpl_merch() {
        return naznpl_merch;
    }

    public void setNaznpl_merch(String naznpl_merch) {
        this.naznpl_merch = naznpl_merch;
    }

    public String getBclassd_merch() {
        return bclassd_merch;
    }

    public void setBclassd_merch(String bclassd_merch) {
        this.bclassd_merch = bclassd_merch;
    }

    public String getKod_merch() {
        return kod_merch;
    }

    public void setKod_merch(String kod_merch) {
        this.kod_merch = kod_merch;
    }

    public String getKnp_merch() {
        return knp_merch;
    }

    public void setKnp_merch(String knp_merch) {
        this.knp_merch = knp_merch;
    }

    public String getRnna_merch() {
        return rnna_merch;
    }

    public void setRnna_merch(String rnna_merch) {
        this.rnna_merch = rnna_merch;
    }

    public String getPlatel_merch() {
        return platel_merch;
    }

    public void setPlatel_merch(String platel_merch) {
        this.platel_merch = platel_merch;
    }

    @Override
    public String toString() {
        return iik + " " +
                bik + " " +
                " " +
                iinbin + " " +
                amount + " " +
                bankname + " " +
                naznpl_merch + " " +
                bclassd_merch + " " +
                kod_merch + " " +
                knp_merch + " " +
                rnna_merch + " " +
                platel_merch + "\n";
    }
}
