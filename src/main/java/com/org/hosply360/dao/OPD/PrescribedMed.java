package com.org.hosply360.dao.OPD;

import com.org.hosply360.dao.globalMaster.MedicineMaster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrescribedMed {

    @DBRef
    private MedicineMaster medicineMaster;

    @Field("dose")
    private String dose;

    @Field("when")
    private String when;

    @Field("frequency")
    private String frequency;

    @Field("duration")
    private String duration;

    @Field("notes")
    private String notes;
}
