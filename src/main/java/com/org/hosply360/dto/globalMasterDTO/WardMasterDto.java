package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.Status;
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
public class WardMasterDto {

    private String id;

    private String orgId;

    private String wardName;

    private Status status;

    private boolean defunct;

}
