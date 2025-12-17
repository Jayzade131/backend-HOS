    package com.org.hosply360.dto.frontDeskDTO;

    import com.org.hosply360.constant.Enums.DoctorType;
    import com.org.hosply360.constant.Enums.Gender;
    import com.org.hosply360.constant.Enums.Status;
    import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
    import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
    import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
    import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
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
    public class DoctorDTO {

        private String id;

        private String firstName;

        private String shortName;

        private SpecialityDTO specialty;

        private SpecialityDTO department;

        private String qualification;

        private String postQualification;

        private Long experience;

        private String registrationNo;

        private List<LanguageDTO> language;

        private Gender gender;

        private String nationality;

        private AddressDTO permanentAddress;

        private String phoneNo;

        private String mobileNo;

        private String email;

        private boolean externalDoctor;

        private boolean defunct;

        private List<OrganizationDTO>  organization;

        private Double firstVisitRate;

        private Double secondVisitRate;

        private String doctorUserId;

        private String username;

        private Status status;

        private List<DoctorDocumentResDTO> doctorDocument;

        private DoctorType doctorType;

        private Double totalFirstVisitRate;

        private Double totalSecondVisitRate;

        private List<DoctorTariffDTO> doctorTariff;
    }
