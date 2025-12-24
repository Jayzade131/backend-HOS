package com.org.hosply360.dto.pathologyDTO;

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
public class BillSummaryDTO {
    private double total;
    private double paid;
    private double balance;
}
