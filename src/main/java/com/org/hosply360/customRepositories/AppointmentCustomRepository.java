package com.org.hosply360.customRepositories;

import com.org.hosply360.dto.OPDDTO.AppointmentWithInvoiceDTO;
import com.org.hosply360.dto.OPDDTO.PagedResult;

import java.time.LocalDateTime;

public interface AppointmentCustomRepository {

    PagedResult<AppointmentWithInvoiceDTO>findAppointmentsFilteredAndSorted(
            String id, String pId, Boolean isWalkIn, String doctorId, String orgId,
            LocalDateTime fromDate, LocalDateTime toDate,
            int page, int size);


}




