package com.org.hosply360.dao.frontDeskDao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMiscellaneous {

    @Field("referred_by")
    @DBRef
        private Doctor ReferredBy;

}
