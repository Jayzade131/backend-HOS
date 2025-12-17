        package com.org.hosply360.dao.frontDeskDao;

        import com.org.hosply360.constant.Enums.DoctorType;
        import com.org.hosply360.constant.Enums.Gender;
        import com.org.hosply360.constant.Enums.Status;
        import com.org.hosply360.dao.auth.Users;
        import com.org.hosply360.dao.globalMaster.Address;
        import com.org.hosply360.dao.globalMaster.Language;
        import com.org.hosply360.dao.globalMaster.Organization;
        import com.org.hosply360.dao.globalMaster.Speciality;
        import com.org.hosply360.dao.other.BaseModel;
        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;
        import org.springframework.data.annotation.Id;
        import org.springframework.data.mongodb.core.index.Indexed;
        import org.springframework.data.mongodb.core.mapping.DBRef;
        import org.springframework.data.mongodb.core.mapping.Document;
        import org.springframework.data.mongodb.core.mapping.Field;

        import java.io.Serializable;
        import java.util.List;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Document(collection = "doctor_master")
        public class Doctor extends BaseModel implements Serializable {

            @Id
            private String id;

            @Field("firstName")
            private String firstName;

            @Field("short_name")
            private String shortName;

            @DBRef
            @Field("specialty")
            private Speciality specialty;

            @DBRef
            @Field("depart")
            private Speciality depart;  //department

            @Field("qualification")
            private String qualification;

            @Field("post_qualification")
            private String postQualification;

            @Field("experience")
            private Long experience;

            @Field("registration_no")
            @Indexed(unique = true)
            private String registrationNo;

            @DBRef
            @Field("language")
            private List<Language> language;

            @Field("gender")
            private Gender gender;

            @Field("nationality")
            private String nationality;

            @DBRef
            @Field("permanent_address")
            private Address permanentAddress;

            @Field("phone_no")
            private String phoneNo;

            @Field("mobile_no")
            private String mobileNo;

            @Field("email")
            private String email;

            @Field("external_doctor")
            private boolean externalDoctor;

            @Field("defunct")
            private boolean defunct;

            @DBRef
            private List<Organization> organization;

            @Field("first_visit_rate")
            private Double firstVisitRate;

            @Field("second_visit_rate")
            private Double secondVisitRate;

           @Field("status")
           private Status status;

           @Field("documents")
           private List<DoctorDocument> doctorDocument;

           @DBRef
           private Users user;

           private DoctorType doctorType;

           @Field("total_first_rate")
           private Double totalFirstVisitRate;

           @Field("total_second_rate")
           private Double totalSecondVisitRate;

           private List<DoctorTariff> doctorTariff;

        }
