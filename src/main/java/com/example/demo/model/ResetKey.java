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
@Table(name = "reset_key", schema = "demo")
@SequenceGenerator(name = "id_seq_generator", sequenceName = "reset_key_id_seq", schema = "demo", allocationSize = 1)
public class ResetKey extends AbstractEntity {

    @Column(name = "reset_key", length = 30)
    private String resetKey;

    @Column(name = "reset_date")
    private Timestamp resetDate;
  
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Timestamp getResetDate() {
        return resetDate;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate == null ? null : new Timestamp(resetDate.getTime());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
