package com.org.hosply360.dao.frontDeskDao;


import com.org.hosply360.dao.globalMaster.Language;
import com.org.hosply360.dao.globalMaster.Religion;
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
public class PatientDemographicInformation {

    @Field("language_preference")
    @DBRef
    private Language languagePreference;

    @Field("religion")
    @DBRef
    private Religion religion;

}


