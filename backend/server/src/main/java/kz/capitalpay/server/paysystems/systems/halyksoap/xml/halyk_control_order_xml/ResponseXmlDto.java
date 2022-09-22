package kz.capitalpay.server.paysystems.systems.halyksoap.xml.halyk_control_order_xml;

import javax.xml.bind.annotation.XmlAttribute;

public class ResponseXmlDto {
    private String code;
    private String message;
    private String remainingAmount;

    @XmlAttribute(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlAttribute(name = "code")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlAttribute(name = "remainingAmount")
    public String getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    @Override
    public String toString() {
        return "ResponseXmlDto{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", remainingAmount='" + remainingAmount + '\'' +
                '}';
    }
}
