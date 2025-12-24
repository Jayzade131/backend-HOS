package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathologyPaymentUpdateDTO {
    private String testManagerId;
    private String orgId;
    private PaymentMode paymentType;
    private Double newAmount;
    private String chequeNumber;
    private String bankName;
    private LocalDate chequeDate;
}
