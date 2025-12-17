package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.ipd.DocumentHeadType;
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
public class IPDDocumentResponseDTO {

    private String id;

    private DocumentHeadType head;

    private String ipdAdmissionId;

    private String docName;

    private String remark;

    private String uploadedBy;

    private LocalDateTime uploadedDate;

    private Boolean defunct;
}
