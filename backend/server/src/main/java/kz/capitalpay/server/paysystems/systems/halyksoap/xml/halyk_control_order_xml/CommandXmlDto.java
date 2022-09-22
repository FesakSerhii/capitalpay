package kz.capitalpay.server.paysystems.systems.halyksoap.xml.halyk_control_order_xml;

import javax.xml.bind.annotation.XmlAttribute;

public class CommandXmlDto {

    private String type;

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CommandXmlDto{" +
                "type='" + type + '\'' +
                '}';
    }
}
