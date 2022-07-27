package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlAttribute;

public class DepartmentXmlDto {
    private String merchantId;
    private String abonentId;
    private String serviceId;

    @XmlAttribute(name = "merchant_id")
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @XmlAttribute(name = "abonent_id")
    public String getAbonentId() {
        return abonentId;
    }

    public void setAbonentId(String abonentId) {
        this.abonentId = abonentId;
    }

    @XmlAttribute(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "{" +
                "merchantId='" + merchantId + '\'' +
                ", abonentId='" + abonentId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}
