package com.org.hosply360.dto.IPDDTO;


import com.org.hosply360.constant.Enums.AdmitStatus;
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
public class BedDataDTO {

    private String wardId;
    private String wardName;
    private String bedId;
    private String bedNo;
    private LocalDateTime admitDateTime;
    private AdmitStatus status;
    private String encryptedFirstName;
    private String encryptedLastName;
    private String patientName;
}
