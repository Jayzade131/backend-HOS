package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.AppointmentTokenCounter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentTokenCounterRepository extends MongoRepository<AppointmentTokenCounter, String> {
    List<AppointmentTokenCounter> findByAppointmentDayBefore(String date);
    void deleteByAppointmentDayBefore(String date);
}