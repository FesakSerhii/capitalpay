package kz.capitalpay.server.paysystems.systems.halyksoap.xml.halyk_control_order_xml;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class BankXmlDto {
    private String name;
    private String merchantSign;
    private MerchantXmlDto merchant;
    private ResponseXmlDto response;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "merchant_sign")
    public String getMerchantSign() {
        return merchantSign;
    }

    public void setMerchantSign(String merchantSign) {
        this.merchantSign = merchantSign;
    }

    @XmlElement
    public MerchantXmlDto getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantXmlDto merchant) {
        this.merchant = merchant;
    }

    @XmlElement
    public ResponseXmlDto getResponse() {
        return response;
    }

    public void setResponse(ResponseXmlDto response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "BankXmlDto{" +
                "name='" + name + '\'' +
                ", merchantSign='" + merchantSign + '\'' +
                ", merchant=" + merchant.toString() +
                ", response=" + response.toString() +
                '}';
    }
}
