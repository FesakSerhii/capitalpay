package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class CustomerXmlDto {
    private String name;
    private String mail;
    private String phone;
    private String lang;
    private MerchantXmlDto merchant;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @XmlAttribute
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @XmlAttribute
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @XmlElement
    public MerchantXmlDto getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantXmlDto merchant) {
        this.merchant = merchant;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", lang='" + lang + '\'' +
                ", merchant=" + merchant +
                '}';
    }
}
