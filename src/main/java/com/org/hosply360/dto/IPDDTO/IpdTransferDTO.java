package com.org.hosply360.dto.IPDDTO;

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
public class IpdTransferDTO {

    private String id;
    private String ipdAdmissionId;
    private String currentWardId;
    private String transferWardId;
    private String currentBedId;
    private String transferBedId;
    private String remark;
    private LocalDateTime dateTime;
    private boolean defunct;
}
