package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

    private String id;


    private String street;

    private String buildingFlat;

    private Long cityId;

    private String cityName;

    private Long stateId;

    private String stateName;

    private Long countryId;

    private String countryName;

    private String pinCode;
}
