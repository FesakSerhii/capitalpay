package kz.capitalpay.server.paysystems.systems.halyksoap.xml.halyk_control_order_xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MerchantXmlDto {
    private String id;
    private String reason;
    private CommandXmlDto command;
    private PaymentXmlDto payment;


    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @XmlElement
    public CommandXmlDto getCommand() {
        return command;
    }

    public void setCommand(CommandXmlDto command) {
        this.command = command;
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
        return "MerchantXmlDto{" +
                "id='" + id + '\'' +
                ", reason='" + reason + '\'' +
                ", command=" + command.toString() +
                ", payment=" + payment.toString() +
                '}';
    }
}
