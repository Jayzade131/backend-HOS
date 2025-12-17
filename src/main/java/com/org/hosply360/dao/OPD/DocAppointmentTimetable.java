package com.org.hosply360.dao.OPD;

import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.dao.globalMaster.Speciality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "doc_appointment_timetable")
public class DocAppointmentTimetable extends BaseModel {

    @Id
    private String id;

    @Field("doctor_id")
    @DBRef
    private Doctor doctorId;

    @Field("specialty_Id")
    @DBRef
    private Speciality specialtyId;

    @Field("organization_id")
    @DBRef
    private Organization organizationId;

    @Field("weekly_schedule")
    private Map<String, WeeklySchedule> weeklySchedule;

    @Field("defunct")
    private boolean defunct;

}
