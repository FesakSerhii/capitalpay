package kz.capitalpay.server.registerpayments.dto;

public class RegisterPaymentDTO {
    private String receivingMerchantBank;
    private String binMerchant;
    private String totalAmountPayment;
    private String merchant;
    private String purposePayment;
    private String budgetCode;
    private String codeSenderCash;
    private String codePurposePayment;
    private String iinContributor;
    private String contributorName;

    public String getReceivingMerchantBank() {
        return receivingMerchantBank;
    }

    public void setReceivingMerchantBank(String receivingMerchantBank) {
        this.receivingMerchantBank = receivingMerchantBank;
    }

    public String getBinMerchant() {
        return binMerchant;
    }

    public void setBinMerchant(String binMerchant) {
        this.binMerchant = binMerchant;
    }

    public String getTotalAmountPayment() {
        return totalAmountPayment;
    }

    public void setTotalAmountPayment(String totalAmountPayment) {
        this.totalAmountPayment = totalAmountPayment;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
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
        return
                "receivingMerchantBank='" + receivingMerchantBank + '\'' +
                ", binMerchant='" + binMerchant + '\'' +
                ", totalAmountPayment='" + totalAmountPayment + '\'' +
                ", merchant='" + merchant + '\'' +
                ", purposePayment='" + purposePayment + '\'' +
                ", budgetCode='" + budgetCode + '\'' +
                ", codeSenderCash='" + codeSenderCash + '\'' +
                ", codePurposePayment='" + codePurposePayment + '\'' +
                ", iinContributor='" + iinContributor + '\'' +
                ", contributorName='" + contributorName + '\'' + "\n";
    }
}
