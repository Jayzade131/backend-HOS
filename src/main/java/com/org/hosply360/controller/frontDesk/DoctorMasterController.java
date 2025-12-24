package com.org.hosply360.controller.frontDesk;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorReqDTO;
import com.org.hosply360.service.frontdesk.DoctorMasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class DoctorMasterController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorMasterController.class);
    private final DoctorMasterService doctorMasterService;

    @PostMapping(value = EndpointConstants.CREATE_MODIFY_DOCTOR_API, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppResponseDTO> createUpdateDoctor(@RequestBody DoctorReqDTO doctorReqDTO) throws IOException {
        logger.info("Creating doctor with registration no: {}", doctorReqDTO.getRegistrationNo());

        return ResponseEntity.ok(AppResponseDTO.ok(doctorMasterService.createDoctor(doctorReqDTO)));
    }


    @GetMapping(EndpointConstants.GET_DOCTORS_API)
    public ResponseEntity<AppResponseDTO> getAllDoctors(@RequestParam String organizationId) {
        return ResponseEntity.ok(AppResponseDTO.ok(doctorMasterService.getAllDoctors(organizationId)));

    }

    @GetMapping(EndpointConstants.DOCTOR_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getDoctorById(@PathVariable String id) {
        logger.info("Fetching doctor with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(doctorMasterService.getDoctorById(id)));
    }

    @GetMapping(EndpointConstants.DOCTOR_WITH_SPECIALITY)
    public ResponseEntity<AppResponseDTO> getAllDoctorsBySpeciality(@RequestParam String speId, @RequestParam String organizationId) {
        logger.info("Fetching doctor with Speciality ID: {}", speId);
        return ResponseEntity.ok(AppResponseDTO.ok(doctorMasterService.getAllDoctorsBySpeciality(speId, organizationId)));
    }


    @DeleteMapping(EndpointConstants.DOCTOR_API_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteDoctorById(@PathVariable String id) {
        doctorMasterService.deleteDoctor(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping(EndpointConstants.FETCH_ALL_DOCTOR)
    public ResponseEntity<AppResponseDTO> fetchAllDoctor(@PathVariable String organizationId) {
        logger.info("Fetching all doctors");
        return ResponseEntity.ok(AppResponseDTO.ok(doctorMasterService.fetchAllDoctor(organizationId)));
    }

    @GetMapping(EndpointConstants.GET_DOCTORS_BY_DOCTOR_TYPE_API)
    public ResponseEntity<AppResponseDTO> getAllByDoctorType(@RequestParam DoctorType doctorType, @RequestParam String organizationId) {
        return ResponseEntity.ok(AppResponseDTO.ok(doctorMasterService.getDoctorByDoctorType(doctorType, organizationId)));
    }
}
