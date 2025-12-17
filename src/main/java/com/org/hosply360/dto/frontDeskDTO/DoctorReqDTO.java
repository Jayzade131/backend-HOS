package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.constant.Enums.Gender;
import com.org.hosply360.constant.Enums.Status;
import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorReqDTO {

    private String id;

    private String firstName;

    private String shortName;

    private String specialtyId;

    private String  departmentId;

    private String qualification;

    private String postQualification;

    private Long experience;

    private String registrationNo;

    private List<String> languageId;

    private Gender gender;

    private String nationality;

    private AddressDTO permanentAddress;

    private String phoneNo;

    private String mobileNo;

    private String email;

    private boolean externalDoctor;

    private boolean defunct;

    private List<String> orgIds;

    private Double firstVisitRate;

    private Double secondVisitRate;

    private List<DoctorDocumentDTO> doctorDocumentDTOS;

    private Status status;

    private String doctorUserId;

    private DoctorType doctorType;

    private List<DoctorTariffDTO> doctorTariffDTOS;
}
