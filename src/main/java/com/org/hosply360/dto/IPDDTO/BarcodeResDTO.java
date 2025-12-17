package com.org.hosply360.dto.IPDDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BarcodeResDTO {
    private String barcodeText;
    private String barcodeImage;

}
