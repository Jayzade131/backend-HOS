package com.org.hosply360.dto.utils;

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
public class PdfHeaderFooterDTO {

    private byte[] logo;
    private String hospitalName;
    private String tagline;
    private String address;
    private String contactNo;
    private String gst;
    private String website;
    private String email;
}
