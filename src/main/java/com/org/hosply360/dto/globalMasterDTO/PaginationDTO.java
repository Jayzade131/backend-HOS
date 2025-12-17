package com.org.hosply360.dto.globalMasterDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginationDTO {
    private long total;
    private long totalPages;
    private long pageNumber;
}
