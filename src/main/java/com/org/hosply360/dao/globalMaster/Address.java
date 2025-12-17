package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "address_master")
public class Address extends BaseModel implements Serializable {

    @Id
    private String id;

    @Field("street")
    private String street;

    @Field("building_flat")
    private String buildingFlat;

    @Field("city")
    private Long cityId;

    @Field("city_name")
    private String cityName;

    @Field("state")
    private  Long stateId;

    @Field("state_name")
    private String stateName;

    @Field("country")
    private Long countryId;

    @Field("country_name")
    private String countryName;

    @Field("pin_code")
    private String pinCode;
}
