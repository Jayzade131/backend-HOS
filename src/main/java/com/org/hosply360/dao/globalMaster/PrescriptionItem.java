package com.org.hosply360.dao.globalMaster;


import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "prescription_master")
public class PrescriptionItem  extends BaseModel {

    @Id
    @Field("prescription_id")
    private String id;
    @Field("medicine")
    public MedicineMaster medicine;
    @Field("dose")
    public String dose;
    @Field("when")
    public String when;
    @Field("frequency")
    public String frequency;
    @Field("duration")
    public String duration;
    @Field("notes")
    public String notes;


}
