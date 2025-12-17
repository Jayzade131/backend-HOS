package com.org.hosply360.dto.IPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpdTransferHistoryDto {

    private String transferId;
    private String dateTime;
    private String fromWardName;
    private String fromBedName;
    private String toWardName;
    private String toBedName;
    private String remark;
    private String createdByName;
}
