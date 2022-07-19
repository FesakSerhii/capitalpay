package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ResultsXmlDto {
    private String timestamp;
    private PaymentXmlDto payment;

    @XmlElement
    public PaymentXmlDto getPayment() {
        return payment;
    }

    public void setPayment(PaymentXmlDto payment) {
        this.payment = payment;
    }

    @XmlAttribute
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp='" + timestamp + '\'' +
                ", payment=" + payment +
                '}';
    }
}
