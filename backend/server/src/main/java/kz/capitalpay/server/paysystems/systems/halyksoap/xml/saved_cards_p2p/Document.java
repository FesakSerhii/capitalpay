package kz.capitalpay.server.paysystems.systems.halyksoap.xml.saved_cards_p2p;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Document {
    private ErrorXmlDto error;
    private RequestXmlDto request;
    private PaymentXmlDto payment;

    @XmlElement
    public ErrorXmlDto getError() {
        return error;
    }

    public void setError(ErrorXmlDto error) {
        this.error = error;
    }

    @XmlElement
    public RequestXmlDto getRequest() {
        return request;
    }

    public void setRequest(RequestXmlDto request) {
        this.request = request;
    }

    @XmlElement
    public PaymentXmlDto getPayment() {
        return payment;
    }

    public void setPayment(PaymentXmlDto payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "{" +
                "error=" + error +
                ", request=" + request +
                ", payment=" + payment +
                '}';
    }
}
