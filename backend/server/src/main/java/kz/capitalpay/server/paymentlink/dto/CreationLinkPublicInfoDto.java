package kz.capitalpay.server.paymentlink.dto;

public class CreationLinkPublicInfoDto {
    private String companyName;
    private String contactPhone;

    public CreationLinkPublicInfoDto(String companyName, String contactPhone) {
        this.companyName = companyName;
        this.contactPhone = contactPhone;
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
