package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RegisterPaymentsHalykDTO {
    private String kobd;
    private String lskor;
    private String rnnb;
    private BigDecimal amount;
    private String poluch;
    private String naznpl;
    private String bclassd;
    private String kod;
    private String knp;
    private String rnna;
    private String platel;
    private String bik;

    public String getKobd() {
        return kobd;
    }

    public void setKobd(String kobd) {
        this.kobd = kobd;
    }

    public String getLskor() {
        return lskor;
    }

    public void setLskor(String lskor) {
        this.lskor = lskor;
    }

    public String getRnnb() {
        return rnnb;
    }

    public void setRnnb(String rnnb) {
        this.rnnb = rnnb;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPoluch() {
        return poluch;
    }

    public void setPoluch(String poluch) {
        this.poluch = poluch;
    }

    public String getNaznpl() {
        return naznpl;
    }

    public void setNaznpl(String naznpl) {
        this.naznpl = naznpl;
    }

    public String getBclassd() {
        return bclassd;
    }

    public void setBclassd(String bclassd) {
        this.bclassd = bclassd;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getKnp() {
        return knp;
    }

    public void setKnp(String knp) {
        this.knp = knp;
    }

    public String getRnna() {
        return rnna;
    }

    public void setRnna(String rnna) {
        this.rnna = rnna;
    }

    public String getPlatel() {
        return platel;
    }

    public void setPlatel(String platel) {
        this.platel = platel;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    @Override
    public String toString() {
        return bik + "¤" +
                lskor + "¤¤" +
                rnnb + "¤" +
                amount.setScale(2, RoundingMode.HALF_DOWN) + "¤" +
                poluch + "¤" +
                naznpl + "¤      ¤" +
//                bclassd + "¤" +
                kod + "¤" +
                knp + "¤¤¤\r\n";
//                knp + "¤" +
//                rnna + "¤" +
//                platel + "¤¤¤\r\n";
    }
}
