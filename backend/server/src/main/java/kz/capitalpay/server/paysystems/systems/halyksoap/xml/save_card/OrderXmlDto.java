package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class OrderXmlDto {
    private String id;
    private String currency;
    private String amount;
    private DepartmentXmlDto department;

    @XmlAttribute(name = "order_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlAttribute(name = "amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlElement
    public DepartmentXmlDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentXmlDto department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", currency='" + currency + '\'' +
                ", amount='" + amount + '\'' +
                ", department=" + department +
                '}';
    }
}
