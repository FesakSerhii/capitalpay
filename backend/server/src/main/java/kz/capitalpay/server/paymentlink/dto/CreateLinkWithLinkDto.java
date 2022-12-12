package kz.capitalpay.server.paymentlink.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateLinkWithLinkDto {
    @NotBlank
    private String creationLinkId;
    @NotBlank
    private String description;
    @NotBlank
    private String billId;
    @NotNull
    private BigDecimal totalAmount;
    @Email
    private String payerEmail;
    private String emailTitle;
    private String emailText;
    @NotNull
    private Integer validHours;
    private List<Long> fileIds;

    public String getCreationLinkId() {
        return creationLinkId;
    }

    public void setCreationLinkId(String creationLinkId) {
        this.creationLinkId = creationLinkId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public void setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    public Integer getValidHours() {
        return validHours;
    }

    public void setValidHours(Integer validHours) {
        this.validHours = validHours;
    }

    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }
}
