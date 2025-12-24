package com.org.hosply360.util.Others;

import com.org.hosply360.helper.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtilis {
    public static String getLoggedInUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails principal) {
            return principal.getUser().getUsername();
        }

        return null; // or throw custom exception as per your standard
    }

}
