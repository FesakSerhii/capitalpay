package kz.capitalpay.server.terminal.dto;

public class MerchantTerminalSettingsDto {
    private Long packageId;
    private Long inputTerminalId;
    private Long outputTerminalId;
    private String terminalName;
    private Long merchantId;

    public MerchantTerminalSettingsDto(Long packageId, Long inputTerminalId, Long outputTerminalId, String terminalName, Long merchantId) {
        this.packageId = packageId;
        this.inputTerminalId = inputTerminalId;
        this.outputTerminalId = outputTerminalId;
        this.terminalName = terminalName;
        this.merchantId = merchantId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getInputTerminalId() {
        return inputTerminalId;
    }

    public void setInputTerminalId(Long inputTerminalId) {
        this.inputTerminalId = inputTerminalId;
    }

    public Long getOutputTerminalId() {
        return outputTerminalId;
    }

    public void setOutputTerminalId(Long outputTerminalId) {
        this.outputTerminalId = outputTerminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
