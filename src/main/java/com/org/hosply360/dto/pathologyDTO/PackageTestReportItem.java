package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.dao.pathology.TestReportParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageTestReportItem extends BaseModel {
    @DBRef
    private Test test;
    private LocalDateTime reportDate;
    private List<TestReportParameter> parameters;

}
