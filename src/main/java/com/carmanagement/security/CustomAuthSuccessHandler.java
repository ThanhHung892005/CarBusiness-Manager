package com.carmanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        String redirectUrl;
        if (roles.contains("ROLE_ADMIN")) {
            redirectUrl = "/admin/dashboard";
        } else if (roles.contains("ROLE_MANAGER")) {
            redirectUrl = "/manager/dashboard";
        } else if (roles.contains("ROLE_SALES")) {
            redirectUrl = "/sales/dashboard";
        } else if (roles.contains("ROLE_CUSTOMER")) {
            redirectUrl = "/customer/dashboard";
        } else {
            redirectUrl = "/";
        }

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
