package com.org.hosply360.dao.IPD;


import com.org.hosply360.dao.globalMaster.WardBedMaster;
import com.org.hosply360.dao.globalMaster.WardMaster;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ipd_transfer")
public class IpdTransfer extends BaseModel {

    @Id
    private String id;

    @DBRef
    private IPDAdmission ipdAdmission;

    private LocalDateTime dateTime;

    @DBRef
    private WardMaster currentWard;

    @DBRef
    private WardMaster transferWard;

    @DBRef
    private WardBedMaster currentBed;

    @DBRef
    private WardBedMaster transferBed;

    private String remark;

    private boolean defunct;
}