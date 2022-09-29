package kz.capitalpay.server.paysystems.systems.halyksoap.xml.save_card;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class BankXmlDto {
    private String name;
    private ResultsXmlDto results;
    private CustomerXmlDto customer;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public ResultsXmlDto getResults() {
        return results;
    }

    public void setResults(ResultsXmlDto results) {
        this.results = results;
    }

    public CustomerXmlDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerXmlDto customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "{" + "name='" + name + '\'' + ", results=" + results + ", customer=" + customer + '}';
    }
}
