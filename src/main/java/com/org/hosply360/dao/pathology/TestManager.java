package com.org.hosply360.dao.pathology;

import com.org.hosply360.constant.Enums.TestSource;
import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.PackageE;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "test_manager")
public class TestManager extends BaseModel {

    @Id
    private String id;

    @DBRef
    private Organization organization;

    @DBRef
    private Patient patient;

    @DBRef
    private PackageE packageE;

    @DBRef
    private List<Test> test;

    private TestSource source;

    private TestStatus status;
    private LocalDateTime testDateTime;

    private Double totalAmount;

    private Boolean hasPaid;

    private Double paidAmount;

    private Boolean defunct;
}
