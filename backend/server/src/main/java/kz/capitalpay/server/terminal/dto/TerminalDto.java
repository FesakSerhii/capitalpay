package kz.capitalpay.server.terminal.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TerminalDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    private Long inputTerminalId;
    @NotNull
    private Long outputTerminalId;
    @NotNull
    private boolean p2p;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isP2p() {
        return p2p;
    }

    public void setP2p(boolean p2p) {
        this.p2p = p2p;
    }
}
