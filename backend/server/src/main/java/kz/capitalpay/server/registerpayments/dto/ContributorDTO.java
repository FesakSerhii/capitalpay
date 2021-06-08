package kz.capitalpay.server.registerpayments.dto;

import java.time.LocalDateTime;

public class ContributorDTO {
    private LocalDateTime periodRegisterPayments;
    private String purposePayment;
    private String budgetCode;
    private String codeSenderCash;
    private String codePurposePayment;
    private String iinContributor;
    private String contributorName;

    public LocalDateTime getPeriodRegisterPayments() {
        return periodRegisterPayments;
    }

    public void setPeriodRegisterPayments(LocalDateTime periodRegisterPayments) {
        this.periodRegisterPayments = periodRegisterPayments;
    }

    public String getPurposePayment() {
        return purposePayment;
    }

    public void setPurposePayment(String purposePayment) {
        this.purposePayment = purposePayment;
    }

    public String getBudgetCode() {
        return budgetCode;
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
    }

    public String getCodeSenderCash() {
        return codeSenderCash;
    }

    public void setCodeSenderCash(String codeSenderCash) {
        this.codeSenderCash = codeSenderCash;
    }

    public String getCodePurposePayment() {
        return codePurposePayment;
    }

    public void setCodePurposePayment(String codePurposePayment) {
        this.codePurposePayment = codePurposePayment;
    }

    public String getIinContributor() {
        return iinContributor;
    }

    public void setIinContributor(String iinContributor) {
        this.iinContributor = iinContributor;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    @Override
    public String toString() {
        return "ContributorDTO{" +
                "periodRegisterPayments=" + periodRegisterPayments +
                ", purposePayment='" + purposePayment + '\'' +
                ", budgetCode='" + budgetCode + '\'' +
                ", codeSenderCash='" + codeSenderCash + '\'' +
                ", codePurposePayment='" + codePurposePayment + '\'' +
                ", iinContributor='" + iinContributor + '\'' +
                ", contributorName='" + contributorName + '\'' +
                '}';
    }
}
