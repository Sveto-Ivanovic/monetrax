package com.monetrax.monetrax.auth.security;

import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private UUID userId;
    private boolean isVerified;
    private UserEntity userEntity;
    List<GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity userEntity){
        this.username = userEntity.getUserEmail();
        this.password = userEntity.getPasswordHash();
        this.userId= userEntity.getUserId();
        this.isVerified = userEntity.isHasVerifiedEmail();
        this.authorities = new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority(userEntity.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId.toString();
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
    public boolean isEnabled() {
        return isVerified;
    }
}
