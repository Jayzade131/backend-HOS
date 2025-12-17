package com.org.hosply360.dto.IPDDTO;


import com.org.hosply360.constant.Enums.ipd.DocumentHeadType;
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
public class IPDDocumentRequestDTO {

    private String id;

    private DocumentHeadType head;

    private String ipdAdmissionId;

    private byte[] doc;

    private String docName;

    private String remark;

    private Boolean defunct;
}
