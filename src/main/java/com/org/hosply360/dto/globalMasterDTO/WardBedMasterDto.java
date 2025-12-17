package com.org.hosply360.dto.globalMasterDTO;
import com.org.hosply360.constant.Enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardBedMasterDto {

    private String id;
    private String orgId;
    private String wardId;
    private String wardName;
    private String bedNo;
    private boolean defunct;
}
