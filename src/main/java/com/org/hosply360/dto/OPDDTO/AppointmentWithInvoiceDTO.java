package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentWithInvoiceDTO {
    private String appointmentId;
    private String aId;
    private String patientId;
    private Integer tokenNumber;
    private String pId;
    private String patientName;
    private String lastName;
    private String patientMoNumber;
    private String age;
    private String gender;
    private LocalDateTime appointmentDate;
    private String session;
    private String startTime;
    private String endTime;
    private AppointmentStatus appointmentStatus;
    private String appointmentType;
    private AppointmentDocInfoDTO docInfo;
    private boolean defunct;
    private Boolean isWalkIn;
    private Double totalAmount;
    private Double paidAmount;
    private Double balanceAmount;
    private String invoiceId;
    private String doctorName;
    private String invoiceNumber;

}
