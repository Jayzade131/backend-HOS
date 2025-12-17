package com.org.hosply360.dto.frontDeskDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {
    private String id;
    @Field("pId")
    private String pId;
    private String firstName;
    private String lastName;
    private String dob;
    private String phoneNumber;
    private String status;

}
