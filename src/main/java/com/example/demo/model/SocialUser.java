package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "social_user", schema = "demo")
@SequenceGenerator(name = "id_seq_generator", sequenceName = "social_user_id_seq", schema = "demo", allocationSize = 1)
public class SocialUser extends AbstractEntity {

    @Column(name = "social_id", length = 128, nullable = false, updatable = false)
    private String socialId;

    @Column(name = "source", length = 16, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private SocialSource source;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    private User user;

    public SocialUser() {
    }

    public SocialUser(String socialId, SocialSource source, User user) {
        this.socialId = socialId;
        this.source = source;
        this.user = user;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String resetKey) {
        this.socialId = resetKey;
    }

    public SocialSource getSource() {
        return source;
    }

    public void setSource(SocialSource source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
