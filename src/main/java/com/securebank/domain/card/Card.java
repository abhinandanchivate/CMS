package com.securebank.domain.card;

import com.securebank.common.model.CardType;
import com.securebank.domain.BaseEntity;
import com.securebank.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class Card extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "masked", nullable = false, length = 32)
    private String masked;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private CardType type;

    @Column(name = "expires", nullable = false, length = 7)
    private YearMonth expires;

    @Column(name = "locked", nullable = false)
    private boolean locked = false;

    @Column(name = "international", nullable = false)
    private boolean international = true;

    @Column(name = "ecom", nullable = false)
    private boolean ecommerce = true;

    @Column(name = "atm_daily_limit")
    private Integer atmDailyLimit;

    @Column(name = "pos_daily_limit")
    private Integer posDailyLimit;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMasked() {
        return masked;
    }

    public void setMasked(String masked) {
        this.masked = masked;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public YearMonth getExpires() {
        return expires;
    }

    public void setExpires(YearMonth expires) {
        this.expires = expires;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }

    public boolean isEcommerce() {
        return ecommerce;
    }

    public void setEcommerce(boolean ecommerce) {
        this.ecommerce = ecommerce;
    }

    public Integer getAtmDailyLimit() {
        return atmDailyLimit;
    }

    public void setAtmDailyLimit(Integer atmDailyLimit) {
        this.atmDailyLimit = atmDailyLimit;
    }

    public Integer getPosDailyLimit() {
        return posDailyLimit;
    }

    public void setPosDailyLimit(Integer posDailyLimit) {
        this.posDailyLimit = posDailyLimit;
    }
}
