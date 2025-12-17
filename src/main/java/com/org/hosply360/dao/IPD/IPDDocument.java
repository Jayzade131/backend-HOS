package com.org.hosply360.dao.IPD;

import com.org.hosply360.constant.Enums.ipd.DocumentHeadType;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ipd_documents")
public class IPDDocument extends BaseModel {

    @Id
    private String id;

    private DocumentHeadType head;

    @DBRef
    private IPDAdmission ipdAdmission;

    private byte[] doc;

    private String docName;

    private String remark;

    private Boolean defunct;
}
