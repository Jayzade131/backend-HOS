package com.org.hosply360.dao.OPD;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Obstetric {

    @Field("last_menstrual_period")
    private String lmp;

    @Field("gravida_para_abortus")
    private String gpa;

    @Field("per_abdomen")
    private String pa;

    @Field("per_speculum")
    private String ps;

    @Field("per_vaginam")
    private String pv;


}
