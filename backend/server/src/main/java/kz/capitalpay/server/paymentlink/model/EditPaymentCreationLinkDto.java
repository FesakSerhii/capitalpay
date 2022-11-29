package kz.capitalpay.server.paymentlink.model;

import javax.validation.constraints.NotEmpty;

public class EditPaymentCreationLinkDto {
    @NotEmpty
    private String id;
    @NotEmpty
    private String companyName;
    @NotEmpty
    private String contactPhone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
