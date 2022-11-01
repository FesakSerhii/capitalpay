package kz.capitalpay.server.terminal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Terminal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long inputTerminalId;
    private Long outputTerminalId;
    private String name;

    @Column(columnDefinition = "boolean default false")
    @JsonIgnore
    private boolean deleted;

    @Column(columnDefinition = "boolean default true")
    private boolean free = true;
    @Column(columnDefinition = "boolean default false")
    private boolean p2p;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isP2p() {
        return p2p;
    }

    public void setP2p(boolean p2p) {
        this.p2p = p2p;
    }
}
