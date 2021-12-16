package kz.capitalpay.server.cashbox.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Cashbox {
    @Id
    @GeneratedValue
    Long id;
    Long merchantId;
    String name;
    @Column(name = "deleted", columnDefinition = "boolean default false", nullable = false)
    boolean deleted;
    Long userCardId;
    @Column(columnDefinition = "boolean default false", nullable = false)
    boolean p2pAllowed;
    @Column(columnDefinition = "boolean default true", nullable = false)
    boolean useDefaultCard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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

    public Long getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(Long userCardId) {
        this.userCardId = userCardId;
    }

    public boolean isP2pAllowed() {
        return p2pAllowed;
    }

    public void setP2pAllowed(boolean p2pAllowed) {
        this.p2pAllowed = p2pAllowed;
    }

    public boolean isUseDefaultCard() {
        return useDefaultCard;
    }

    public void setUseDefaultCard(boolean useDefaultCard) {
        this.useDefaultCard = useDefaultCard;
    }
}
