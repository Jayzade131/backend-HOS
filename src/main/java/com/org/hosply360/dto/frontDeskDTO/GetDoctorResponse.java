package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.constant.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetDoctorResponse implements Serializable {
    private String id;
    private List<String> orgId;
    private String RegNo;
    private String doctorName;
    private String specialityId;
    private String speciality;
    private String department;
    private String firstFee;
    private String secondFee;
    private Status status;
    private String doctorUserId;
    private DoctorType doctorType;
}

