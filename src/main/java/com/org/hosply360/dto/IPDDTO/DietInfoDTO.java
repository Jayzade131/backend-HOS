package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.Diet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DietInfoDTO {
    private Diet diet;
    private LocalDateTime dateTime;
    private String dietTime;
    private String time;
    private String remark;
}
