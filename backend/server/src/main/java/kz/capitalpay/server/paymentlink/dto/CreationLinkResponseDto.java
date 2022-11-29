package kz.capitalpay.server.paymentlink.dto;

public class CreationLinkResponseDto {
    private String id;
    private Long cashBoxId;
    private String companyName;
    private String contactPhone;
    private Long cashboxId;
    private String link;
    private String qr;

    public CreationLinkResponseDto(String id, Long cashBoxId, String companyName, String contactPhone, Long cashboxId, String link, String qr) {
        this.id = id;
        this.cashBoxId = cashBoxId;
        this.companyName = companyName;
        this.contactPhone = contactPhone;
        this.cashboxId = cashboxId;
        this.link = link;
        this.qr = qr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(Long cashBoxId) {
        this.cashBoxId = cashBoxId;
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

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }
}
