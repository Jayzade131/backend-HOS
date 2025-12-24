package com.org.hosply360.util.mapper;

import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dto.pathologyDTO.PatientBillInfoDTO;
import com.org.hosply360.util.Others.AgeUtil;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PatientMapperUtil {

    private PatientMapperUtil() {}

    public static PatientBillInfoDTO buildPatientBillInfo(Patient patient) {
        var personal = patient.getPatientPersonalInformation();
        var contact = patient.getPatientContactInformation();
        var address = contact != null ? contact.getAddress() : null;

        String fullName = Stream.of(
                personal.getFirstName(),
                personal.getLastName()
        ).filter(Objects::nonNull).collect(Collectors.joining(" "));

        String fullAddress = address == null ? null :
                Stream.of(address.getBuildingFlat(), address.getStreet(),
                                address.getCityName(), address.getStateName(), address.getPinCode())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" "));

        return PatientBillInfoDTO.builder()
                .fullName(fullName)
                .pId(patient.getPId())
                .dob(personal.getDateOfBirth())
                .gender(personal.getGender())
                .age(AgeUtil.getAge(personal.getDateOfBirth()))
                .phone(contact != null ? contact.getPrimaryPhone() : null)
                .address(fullAddress)
                .build();
    }
}
