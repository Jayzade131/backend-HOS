package com.org.hosply360.dto.OPDDTO;


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
public class OPDAppointmentBillingResponseDTO {
    private String invoiceId;
    private String invoiceNumber;
    private String pId;
    private String appointmentNo;
    private LocalDateTime date;
    private String name;
    private String ageGender;
    private String mobile;
    private String consultant;
    private String category;
    private String visitType;
    private Double balance;
}
