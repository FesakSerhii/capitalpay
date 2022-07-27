package kz.capitalpay.server.paysystems.systems.halyksoap.xml.saved_cards_p2p;

import javax.xml.bind.annotation.XmlAttribute;

public class RequestXmlDto {
    private String signedOrderB64;

    @XmlAttribute(name = "Signed_Order_B64")
    public String getSignedOrderB64() {
        return signedOrderB64;
    }

    public void setSignedOrderB64(String signedOrderB64) {
        this.signedOrderB64 = signedOrderB64;
    }

    @Override
    public String toString() {
        return "{" +
                "SignedOrderB64='" + signedOrderB64 + '\'' +
                '}';
    }
}
