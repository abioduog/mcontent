/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.util;

import com.mnewservice.mcontent.repository.entity.RoleEntity;
import java.util.EnumSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 */
public class SessionUtils {

    public static EnumSet<RoleEntity.RoleEnum> getCurrentUserRoles() {
        EnumSet<RoleEntity.RoleEnum> roles = EnumSet.noneOf(RoleEntity.RoleEnum.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(authority -> {
            roles.add(RoleEntity.RoleEnum.valueOf(authority.getAuthority()));
        });

        return roles;
    }

    public static String getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
    }


}
