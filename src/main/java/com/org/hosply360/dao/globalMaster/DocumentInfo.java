package com.org.hosply360.dao.globalMaster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentInfo {
    private byte[] docFile;
    private String docName;
}
