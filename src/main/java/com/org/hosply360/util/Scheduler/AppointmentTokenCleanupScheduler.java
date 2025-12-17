package com.org.hosply360.util.Scheduler;

import com.org.hosply360.repository.OPDRepo.AppointmentTokenCounterRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AppointmentTokenCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentTokenCleanupScheduler.class);

    private final AppointmentTokenCounterRepository repository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron ="${Scheduler.TokenCleanerCron}" )
    public void deleteOldTokens() {
        LocalDate cutoffDate = LocalDate.now().minusDays(2);
        String cutoff = cutoffDate.format(FORMATTER);

        logger.info("Running cleanup job. Deleting tokens with appointmentDay before {}", cutoff);

        repository.deleteByAppointmentDayBefore(cutoff);

        logger.info("Cleanup job finished.");
    }
}
