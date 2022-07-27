package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MerchantXmlDto {
    private String certId;
    private String name;
    private OrderXmlDto order;

    @XmlAttribute(name = "cert_id")
    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "order")
    public OrderXmlDto getOrder() {
        return order;
    }

    public void setOrder(OrderXmlDto order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "{" +
                "certId='" + certId + '\'' +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}
