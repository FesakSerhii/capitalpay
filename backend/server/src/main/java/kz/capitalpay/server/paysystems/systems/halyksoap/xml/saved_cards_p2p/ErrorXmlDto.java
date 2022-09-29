package kz.capitalpay.server.paysystems.systems.halyksoap.xml.saved_cards_p2p;

import javax.xml.bind.annotation.XmlAttribute;

public class ErrorXmlDto {
    private String input;
    private String payment;
    private String system;

    @XmlAttribute(name = "input")
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @XmlAttribute(name = "payment")
    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    @XmlAttribute(name = "system")
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Override
    public String toString() {
        return "{" + "input='" + input + '\'' + ", payment='" + payment + '\'' + ", system='" + system + '\'' + '}';
    }
}
