package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Document {
    private BankXmlDto bank;
    private String bankSign;

    @XmlElement
    public BankXmlDto getBank() {
        return bank;
    }

    public void setBank(BankXmlDto bank) {
        this.bank = bank;
    }

    @XmlElement(name = "bank_sign")
    public String getBankSign() {
        return bankSign;
    }

    public void setBankSign(String bankSign) {
        this.bankSign = bankSign;
    }

    @Override
    public String toString() {
        return "{" + "bank=" + bank + ", bankSign='" + bankSign + '\'' + '}';
    }
}
