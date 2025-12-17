package com.org.hosply360.event.listener;

import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.exception.CustomException;
import com.org.hosply360.helper.CustomUserDetails;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditingWhoEventListener extends AbstractMongoEventListener<BaseModel> {


    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseModel> event) {
        BaseModel model = event.getSource();
        String currentUserId = getCurrentUserId();


        if (model.getCreatedDate() == null) {
            model.setCreatedDate(LocalDateTime.now());
            model.setCreatedBy(currentUserId);
        } else {
            model.setUpdatedDate(LocalDateTime.now());
            model.setUpdatedBy(currentUserId);
        }
    }

    private String getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                Object principal = auth.getPrincipal();

                if (principal instanceof CustomUserDetails userDetails) {
                    return userDetails.getUserId();
                }
            }
        } catch (CustomException c) {
            throw new CustomException(c.getMessage(), c.getHttpStatus());
        }
        return "no authenticated user";
    }
}
