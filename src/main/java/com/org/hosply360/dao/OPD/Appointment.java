    package com.org.hosply360.dao.OPD;


    import com.org.hosply360.constant.Enums.AppointmentStatus;
    import com.org.hosply360.dao.frontDeskDao.Doctor;
    import com.org.hosply360.dao.frontDeskDao.Patient;
    import com.org.hosply360.dao.globalMaster.Organization;
    import com.org.hosply360.dao.globalMaster.Speciality;
    import com.org.hosply360.dao.other.BaseModel;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.index.CompoundIndex;
    import org.springframework.data.mongodb.core.index.Indexed;
    import org.springframework.data.mongodb.core.mapping.DBRef;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.Field;
    import org.springframework.data.mongodb.core.mapping.FieldType;
    import org.springframework.data.mongodb.core.mapping.MongoId;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @Document(collection = "Appointment")
    @CompoundIndex(name = "org_date_defunct_idx", def = "{'org.id': 1, 'appointment_date': 1, 'defunct': 1}")
    public class Appointment extends BaseModel {

        @Id
        @Field("id")
        @MongoId(targetType = FieldType.STRING)
        private String id;

        @Field("aId")
        @Indexed(unique = true)
        private String aId;

        @Field("pId")
        @Indexed(unique = true)
        private String pId;

        @Field("token_number")
        private Integer tokenNumber;

        @Field("appointment_day")
        private String appointmentDay;


        @Field("doctor_id")
        @DBRef
        private Doctor doctor;

        @Field("specialty_id")
        @DBRef
        private Speciality specialty;

        @Field("org_id")
        @DBRef
        private Organization org;

        @Field("patient_id")
        @DBRef
        private Patient patient;

        @Field("appointment_date")
        private LocalDateTime appointmentDate;

        @Field("session")
        private String session;

        @Field("start_time")
        private String startTime;

        @Field("end_time")
        private String endTime;

        @Field("status")
        private AppointmentStatus status;

        @Field("appointment_type")
        private String appointmentType;

        @Field("session_start_time")
        private String sessionStartTime;

        @Field("session_end_time")
        private String sessionEndTime;

        @Field("is_walkin")
        private boolean isWalkIn;

        @Field("defunct")
        private boolean defunct;


    }
