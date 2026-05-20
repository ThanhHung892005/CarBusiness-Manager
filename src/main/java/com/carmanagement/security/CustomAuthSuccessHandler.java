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
        if (roles.contains("ROLE_GIAM_DOC")) {
            redirectUrl = "/admin/dashboard";
        } else if (roles.contains("ROLE_NV_KINH_DOANH")) {
            redirectUrl = "/sales/dashboard";
        } else if (roles.contains("ROLE_KE_TOAN")) {
            redirectUrl = "/ke-toan/dashboard";
        } else if (roles.contains("ROLE_THU_KHO")) {
            redirectUrl = "/inventory/dashboard";
        } else {
            redirectUrl = "/login";
        }

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
