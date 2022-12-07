package kz.capitalpay.server.payments.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentFilterDto {

    @NotNull(message = "page cannot be null")
    private Integer page;
    @NotNull(message = "limit cannot be null")
    private Integer limit;
    private @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateBefore;
    private @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateAfter;
    private String currency;
    private String billId;
    private String paymentId;
    private String cashboxName;
    private Long bankTerminalId;
    private BigDecimal totalAmount;
    private Long merchantId;
    private PaymentSortDto sortDto;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public LocalDate getDateBefore() {
        return dateBefore;
    }

    public void setDateBefore(LocalDate dateBefore) {
        this.dateBefore = dateBefore;
    }

    public LocalDate getDateAfter() {
        return dateAfter;
    }

    public void setDateAfter(LocalDate dateAfter) {
        this.dateAfter = dateAfter;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCashboxName() {
        return cashboxName;
    }

    public void setCashboxName(String cashboxName) {
        this.cashboxName = cashboxName;
    }

    public Long getBankTerminalId() {
        return bankTerminalId;
    }

    public void setBankTerminalId(Long bankTerminalId) {
        this.bankTerminalId = bankTerminalId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public PaymentSortDto getSortDto() {
        return sortDto;
    }

    public void setSortDto(PaymentSortDto sortDto) {
        this.sortDto = sortDto;
    }
}
