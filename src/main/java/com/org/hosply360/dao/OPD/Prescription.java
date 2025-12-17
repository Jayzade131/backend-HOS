package com.org.hosply360.dao.OPD;

import com.org.hosply360.constant.Enums.TestWhen;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.dao.globalMaster.Test;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "prescription")
public class Prescription extends BaseModel {

    @Id
    private String id;

    @DBRef
    private Organization organization;

    @Field("prescription_date")
    private LocalDate prescriptionDate;

    @DBRef
    private Patient patient;

    @DBRef
    private Doctor doctor;

    @DBRef
    private Appointment appointment;

    @Field("complaints")
    private String complaints;

    @Field("patient_record")
    private String patientRecord;

    @Field("general_examination")
    private String generalExamination;

    @Field("history")
    private String history;

    @Field("obstetric")
    private Obstetric obstetric;

    @Field("vitals")
    private Vitals vitals;

    @DBRef
    private List<Test> test;

    @Field("test_when")
    private TestWhen testWhen;

    @Field("diagnosis")
    private String diagnosis;

    @Field("prescribed_Meds")
    private List<PrescribedMed> prescribedMeds;

    @Field("advice")
    private String advice;

    @Field("next_visit")
    private String nextVisit;

    @Field("mobile_number")
    private String mobileNumber;

    @Field("email")
    private String email;

    @Field("defunct")
    private boolean defunct;


}
