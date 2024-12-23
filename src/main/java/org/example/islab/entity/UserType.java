package org.example.islab.entity;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    MEMBER,
    ADMIN;

    public boolean lessThan(UserType other){
        return this.ordinal() < other.ordinal();
    }

    @Override
    public String getAuthority() {
        return this.name();
    }

    public static boolean lessThan(String type1, String type2){
        return UserType.valueOf(type1).lessThan(UserType.valueOf(type2));
    }
}
