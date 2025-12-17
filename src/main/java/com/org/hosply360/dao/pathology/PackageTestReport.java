package com.org.hosply360.dao.pathology;

import com.org.hosply360.constant.Enums.PackageTestStatus;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@Builder
@Document(collection = "package_test_report")
public class PackageTestReport extends BaseModel {

    @Id
    private String id;
    @DBRef
    private Organization organization;
    @DBRef
    private TestManager testManager;
    @DBRef
    private Doctor doctor;
    @DBRef
    private Patient patient;
    private LocalDateTime reportDate;
    private List<PackageTestReportItem> packageTestReportItem;
    private Boolean defunct;
    private PackageTestStatus status;

}
