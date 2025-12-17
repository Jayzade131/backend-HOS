package com.org.hosply360.dao.frontDeskDao;

import com.org.hosply360.dao.globalMaster.Tariff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorTariff {
    @DBRef
    private Tariff  tariff;
    private String tariffName;
    private String firstRate;
    private String secondRate;
}
