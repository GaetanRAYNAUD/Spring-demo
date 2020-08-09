package com.example.demo.model;

import com.example.demo.config.filter.GoogleUserDetails;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "app_user", schema = "demo")
@SequenceGenerator(name = "id_seq_generator", sequenceName = "app_user_id_seq", schema = "demo", allocationSize = 1)
public class User extends AbstractEntity implements UserDetails, GoogleUserDetails {

    @Column(name = "username", unique = true, nullable = false, length = 24)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 60)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @Column(name = "password_reset_date")
    private Timestamp passwordResetDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), schema = "demo")
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public SocialUser googleUser;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
        setPasswordResetDate(new Date());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getPasswordResetDate() {
        return passwordResetDate;
    }

    public void setPasswordResetDate(Date resetDate) {
        this.passwordResetDate = resetDate == null ? null : new Timestamp(resetDate.getTime());
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public SocialUser getGoogleUser() {
        return googleUser;
    }

    public void setGoogleUser(SocialUser googleUser) {
        this.googleUser = googleUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getGoogleId() {
        if (this.googleUser == null) {
            return null;
        }

        return this.googleUser.getSocialId();
    }
}
