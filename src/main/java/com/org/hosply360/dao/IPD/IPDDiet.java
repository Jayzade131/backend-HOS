package com.org.hosply360.dao.IPD;

import com.org.hosply360.constant.Enums.Diet;
import com.org.hosply360.constant.Enums.DietTime;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ipd_diet")
public class IPDDiet extends BaseModel {

    @Id
    private String id;
    @DBRef
    private Organization organizationId;
    @DBRef
    private IPDAdmission ipdAdmissionId;
    @Field("date")
    private LocalDateTime dateTime;
    @Field("diet_time")
    private DietTime dietTime;
    @Field("time")
    private String time;
    @Field("diet")
    private Diet diet;
    @Field("remark")
    private String remark;
    @Field("defunct")
    private Boolean defunct;
}
