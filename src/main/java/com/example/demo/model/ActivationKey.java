package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "activation_key", schema = "demo")
@SequenceGenerator(name = "id_seq_generator", sequenceName = "activation_key_id_seq", schema = "demo",  allocationSize = 1)
public class ActivationKey extends AbstractEntity {

    @Column(name = "activation_key", length = 30)
    private String activationKey;

    @Column(name = "activation_date")
    private Timestamp activationDate;
  
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public Timestamp getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate == null ? null : new Timestamp(activationDate.getTime());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
