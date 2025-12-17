package com.org.hosply360.service.OPD.impl;

import com.org.hosply360.constant.Enums.AppointmentStatus;
import com.org.hosply360.constant.Enums.TestWhen;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.OPD.Appointment;
import com.org.hosply360.dao.OPD.Obstetric;
import com.org.hosply360.dao.OPD.PrescribedMed;
import com.org.hosply360.dao.OPD.Prescription;
import com.org.hosply360.dao.OPD.Vitals;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.MedicineMaster;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dto.OPDDTO.AppointmentDTO;
import com.org.hosply360.dto.OPDDTO.ObstetricDTO;
import com.org.hosply360.dto.OPDDTO.PatientUpcomingVisitResponseDTO;
import com.org.hosply360.dto.OPDDTO.PrescribedMedResponseDTO;
import com.org.hosply360.dto.OPDDTO.PrescriptionDTO;
import com.org.hosply360.dto.OPDDTO.PrescriptionPDFResponseDTO;
import com.org.hosply360.dto.OPDDTO.PrescriptionResponseDTO;
import com.org.hosply360.dto.OPDDTO.VitalsDTO;
import com.org.hosply360.dto.globalMasterDTO.MedicineMasterDTO;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.repository.OPDRepo.PrescriptionCustomRepository;
import com.org.hosply360.repository.OPDRepo.PrescriptionRepository;
import com.org.hosply360.repository.frontDeskRepo.PatientRepository;
import com.org.hosply360.repository.globalMasterRepo.MedicineMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.TestRepository;
import com.org.hosply360.service.OPD.AppointmentService;
import com.org.hosply360.service.OPD.PrescriptionService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionServiceImpl.class);

    private final PrescriptionRepository prescriptionRepository;

    private final EntityFetcherUtil entityFetcherUtil;

    private final PatientRepository patientRepository;


    private final AppointmentService appointmentService;

    private final TestRepository testRepository;

    private final MedicineMasterRepository medicineMasterRepository;

    private final PrescriptionCustomRepository prescriptionCustomRepository;

    private static PrescriptionResponseDTO entityToDto(Prescription savePrescription) {
        PrescriptionResponseDTO prescriptionResponseDTO = ObjectMapperUtil.copyObject(savePrescription, PrescriptionResponseDTO.class);

        if (Objects.nonNull(savePrescription.getAppointment())) {
            AppointmentDTO appointmentDTO = ObjectMapperUtil.copyObject(savePrescription.getAppointment(), AppointmentDTO.class);
            if (Objects.nonNull(savePrescription.getAppointment().getStatus())) {
                appointmentDTO.setStatus(savePrescription.getAppointment().getStatus().name());
            }
            prescriptionResponseDTO.setAppointment(appointmentDTO);
        }

        if (Objects.nonNull(savePrescription.getDoctor())) {
            prescriptionResponseDTO.setDoctorId(savePrescription.getDoctor().getId());
            prescriptionResponseDTO.setDoctorName(savePrescription.getDoctor().getFirstName());
        }

        if (Objects.nonNull(savePrescription.getPatient())) {
            prescriptionResponseDTO.setPatientId(savePrescription.getPatient().getId());
            if (Objects.nonNull(savePrescription.getPatient().getPatientPersonalInformation())) {
                prescriptionResponseDTO.setPatientFirstName(savePrescription.getPatient().getPatientPersonalInformation().getFirstName());
                prescriptionResponseDTO.setPatientLastName(savePrescription.getPatient().getPatientPersonalInformation().getLastName());
                prescriptionResponseDTO.setPatientMidName(savePrescription.getPatient().getPatientPersonalInformation().getMiddleName());
            }
        }

        if (Objects.nonNull(savePrescription.getOrganization())) {
            prescriptionResponseDTO.setOrganizationId(savePrescription.getOrganization().getId());
        }

        if (Objects.nonNull(savePrescription.getTest())) {
            prescriptionResponseDTO.setTest(ObjectMapperUtil.copyListObject(savePrescription.getTest(), TestDTO.class));
        }
        if (Objects.nonNull(savePrescription.getTestWhen())) {
            if ((Objects.isNull(savePrescription.getTest()) || savePrescription.getTest().isEmpty())) {
                prescriptionResponseDTO.setTestWhen(TestWhen.NONE);
            } else {
                prescriptionResponseDTO.setTestWhen(savePrescription.getTestWhen());
            }
        } else if (Objects.nonNull(savePrescription.getTest()) && !savePrescription.getTest().isEmpty()) {
            prescriptionResponseDTO.setTestWhen(TestWhen.NEXT_VISIT);
        } else {
            prescriptionResponseDTO.setTestWhen(TestWhen.NONE);
        }

        if (Objects.nonNull(savePrescription.getObstetric())) {
            prescriptionResponseDTO.setObstetric(ObjectMapperUtil.copyObject(savePrescription.getObstetric(), ObstetricDTO.class));
        }

        if (Objects.nonNull(savePrescription.getVitals())) {
            prescriptionResponseDTO.setVitals(ObjectMapperUtil.copyObject(savePrescription.getVitals(), VitalsDTO.class));
        }

        if (Objects.nonNull(savePrescription.getPrescribedMeds())) {
            prescriptionResponseDTO.setPrescribedMeds(
                    savePrescription.getPrescribedMeds().stream()
                            .map(prescribedMed -> {
                                if (Objects.isNull(prescribedMed)) {
                                    return null;
                                }
                                PrescribedMedResponseDTO prescribedMedResponseDTO =
                                        ObjectMapperUtil.copyObject(prescribedMed, PrescribedMedResponseDTO.class);
                                if (Objects.nonNull(prescribedMed.getMedicineMaster())) {
                                    prescribedMedResponseDTO.setMedicine(
                                            ObjectMapperUtil.copyObject(prescribedMed.getMedicineMaster(), MedicineMasterDTO.class)
                                    );
                                }
                                prescribedMedResponseDTO.setWhen(prescribedMed.getWhen());
                                return prescribedMedResponseDTO;
                            })
                            .toList()
            );
        }

        return prescriptionResponseDTO;

    }

    private static Prescription dtoToEntity(PrescriptionDTO prescriptionDTO, Patient patient, Doctor doctor, Organization organization, Appointment appointment, List<Test> test, List<PrescribedMed> listPrescribedMed) {
        Prescription prescription = ObjectMapperUtil.copyObject(prescriptionDTO, Prescription.class);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setOrganization(organization);
        prescription.setAppointment(appointment);
        prescription.setTest(test);
        prescription.setObstetric(ObjectMapperUtil.copyObject(prescriptionDTO.getObstetric(), Obstetric.class));
        prescription.setVitals(ObjectMapperUtil.copyObject(prescriptionDTO.getVitals(), Vitals.class));
        prescription.setPrescribedMeds(listPrescribedMed);
        prescription.setDefunct(false);

        return prescription;
    }

    private List<PrescribedMed> buildPrescribedMeds(PrescriptionDTO prescriptionDTO) {
        if (Objects.isNull(prescriptionDTO) || Objects.isNull(prescriptionDTO.getPrescribedMeds()) || prescriptionDTO.getPrescribedMeds().isEmpty()) {
            return List.of();
        }
        return prescriptionDTO.getPrescribedMeds().stream()
                .filter(Objects::nonNull)
                .map(prescribedMedDTO -> {
                    PrescribedMed prescribedMed = new PrescribedMed();

                    if (Objects.nonNull(prescribedMedDTO.getDose()) && !prescribedMedDTO.getDose().isEmpty()) {
                        prescribedMed.setDose(prescribedMedDTO.getDose());
                    }
                    if (Objects.nonNull(prescribedMedDTO.getFrequency()) && !prescribedMedDTO.getFrequency().isEmpty()) {
                        prescribedMed.setFrequency(prescribedMedDTO.getFrequency());
                    }
                    if (Objects.nonNull(prescribedMedDTO.getDuration()) && !prescribedMedDTO.getDuration().isEmpty()) {
                        prescribedMed.setDuration(prescribedMedDTO.getDuration());
                    }
                    if (Objects.nonNull(prescribedMedDTO.getNotes()) && !prescribedMedDTO.getNotes().isEmpty()) {
                        prescribedMed.setNotes(prescribedMedDTO.getNotes());
                    }
                    if (Objects.nonNull(prescribedMedDTO.getWhen()) && !prescribedMedDTO.getWhen().isEmpty()) {
                        prescribedMed.setWhen(prescribedMedDTO.getWhen());
                    }
                    if (Objects.nonNull(prescribedMedDTO.getMedicineId()) && !prescribedMedDTO.getMedicineId().isEmpty()) {
                        prescribedMed.setMedicineMaster(
                                medicineMasterRepository.findByIdAndDefunct(false, prescribedMedDTO.getMedicineId())
                                        .orElseThrow(() -> new OPDException(ErrorConstant.MEDICINE_NOT_FOUND, HttpStatus.NOT_FOUND))
                        );
                    }
                    if (Objects.isNull(prescribedMed.getDose()) && Objects.isNull(prescribedMed.getFrequency()) && Objects.isNull(prescribedMed.getDuration())
                            && Objects.isNull(prescribedMed.getNotes()) && Objects.isNull(prescribedMed.getWhen()) && Objects.isNull(prescribedMed.getMedicineMaster())) {
                        return null;
                    }
                    return prescribedMed;
                })
                .toList();
    }


    @Override
    public PrescriptionResponseDTO createUpdatePrescription(PrescriptionDTO prescriptionDTO) {
        ValidatorHelper.validateObject(prescriptionDTO);

        Organization organization = entityFetcherUtil.getOrganizationOrThrow(prescriptionDTO.getOrganizationId());
        Patient patient = entityFetcherUtil.getPatientOrThrow(prescriptionDTO.getPatientId());
        Appointment appointment = entityFetcherUtil.getAppointmentOrThrow(prescriptionDTO.getAppointmentId(), prescriptionDTO.getOrganizationId());

        Optional<Prescription> optionalPrescription =
                prescriptionRepository.findByAppointmentIdAndDefunctAndOrg(
                        prescriptionDTO.getAppointmentId(), false, prescriptionDTO.getOrganizationId());

        Doctor doctor = entityFetcherUtil.getDoctorOrThrow(prescriptionDTO.getDoctorId());

        List<Test> tests = null;
        if (Objects.nonNull(prescriptionDTO.getTestId())) {
            if (!prescriptionDTO.getTestId().isEmpty()) {
                tests = testRepository.findAllByDefunctAndIdIn(false, prescriptionDTO.getTestId());
                if (tests.isEmpty()) {
                    throw new OPDException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND);
                }
            }
        }
        if (Objects.isNull(prescriptionDTO.getTestId()) && Objects.nonNull(prescriptionDTO.getTestWhen())) {
            prescriptionDTO.setTestWhen(TestWhen.NONE);
        }

        List<PrescribedMed> listPrescribedMed = null;

        if (Objects.nonNull(prescriptionDTO.getPrescribedMeds())) {
            if (!prescriptionDTO.getPrescribedMeds().isEmpty()) {
                listPrescribedMed = buildPrescribedMeds(prescriptionDTO);

            }
        }
        Prescription prescription;
        if (optionalPrescription.isPresent()) {
            if (optionalPrescription.get().getAppointment().getStatus().equals(AppointmentStatus.PAID)) {
                throw new OPDException(ErrorConstant.DONT_EDIT_THE_PRESCRIPTION_WHEN_APPOINTMENT_STATUS_IS_PAID, HttpStatus.BAD_REQUEST);
            }
            prescription = dtoToEntity(prescriptionDTO, patient, doctor, organization, appointment, tests, listPrescribedMed);
            prescription.setId(optionalPrescription.get().getId());
            prescription = prescriptionRepository.save(prescription);
            logger.info("Prescription updated successfully with ID: {}", prescription.getId());
        } else {
            prescription = dtoToEntity(prescriptionDTO, patient, doctor, organization, appointment, tests, listPrescribedMed);
            prescription = prescriptionRepository.save(prescription);
            logger.info("Prescription created successfully with ID: {}", prescription.getId());
            appointmentService.updateStatus(appointment.getId(), organization.getId(), AppointmentStatus.COMPLETED);
            logger.info("Appointment status updated to COMPLETED for appointment ID: {}", appointment.getId());
        }
        return entityToDto(prescription);

    }

    @Override
    public List<PrescriptionResponseDTO> getPrescriptionByPatientId(String patientId, String orgId, int size) {
        patientRepository.findByIdAndDefunct(patientId, false)
                .orElseThrow(() -> new OPDException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<Prescription> prescriptions =
                prescriptionRepository.getPrescriptionHistoryByPatient(
                        new ObjectId(patientId),
                        false,
                        new ObjectId(orgId)
                );

        return prescriptions.stream()
                .limit(size)
                .map(PrescriptionServiceImpl::entityToDto)
                .toList();
    }

    @Override
    public List<PatientUpcomingVisitResponseDTO> getPatientsByNextVisitRange(String fromDate, String toDate, String doctorId) {
        fromDate = normalizeParam(fromDate);
        toDate = normalizeParam(toDate);
        doctorId = normalizeParam(doctorId);

        LocalDate from = null;
        LocalDate to = null;

        if (fromDate != null && toDate != null) {
            from = LocalDate.parse(fromDate);
            to = LocalDate.parse(toDate);
        }

        return prescriptionCustomRepository.findPatientsByNextVisitRange(from, to, doctorId);
    }

    private String normalizeParam(String param) {
        return (param == null || param.isEmpty()) ? null : param.trim();
    }


    @Override
    public PrescriptionPDFResponseDTO generatePrescriptionPdf(String prescriptionId, String organizationId) {

        Prescription prescription = prescriptionRepository
                .findByIdAndOrganizationId(prescriptionId, organizationId)
                .orElseThrow(() -> new OPDException(ErrorConstant.PRESCRIPTION_NOT_FOUND, HttpStatus.NOT_FOUND));

        PrescriptionPDFResponseDTO dto = new PrescriptionPDFResponseDTO();

        dto.setPdfHeaderFooterDTO(HeaderFooterMapperUtil.buildHeaderFooter(prescription.getOrganization()));
        // ---------- BASIC FIELDS ----------
        dto.setPrescriptionDate(prescription.getPrescriptionDate());
        dto.setPatientFirstName(safe(prescription.getPatient().getPatientPersonalInformation().getFirstName()));
        dto.setPatientLastName(safe(prescription.getPatient().getPatientPersonalInformation().getLastName()));
        dto.setPatientMidName(safe(prescription.getPatient().getPatientPersonalInformation().getMiddleName()));
        dto.setDoctorName(safe(prescription.getDoctor().getFirstName()));
        dto.setComplaints(safe(prescription.getComplaints()));
        dto.setPatientRecord(safe(prescription.getPatientRecord()));
        dto.setGeneralExamination(safe(prescription.getGeneralExamination()));
        dto.setHistory(safe(prescription.getHistory()));

        // ---------- COMPLEX MAPPING ----------
        dto.setObstetric(mapObstetric(prescription.getObstetric()));
        dto.setVitals(mapVitals(prescription.getVitals()));
        dto.setTest(mapTests(prescription.getTest()));
        dto.setTestWhen(TestWhen.valueOf(safe(prescription.getTestWhen())));
        dto.setDiagnosis(safe(prescription.getDiagnosis()));
        dto.setPrescribedMeds(mapPrescribedMeds(prescription.getPrescribedMeds()));
        dto.setAdvice(safe(prescription.getAdvice()));
        dto.setNextVisit(safe(prescription.getNextVisit()));

        return dto;
    }


    private ObstetricDTO mapObstetric(Obstetric obstetric) {
        if (obstetric == null) return new ObstetricDTO();

        ObstetricDTO dto = new ObstetricDTO();
        dto.setLmp(safe(obstetric.getLmp()));
        dto.setGpa(safe(obstetric.getGpa()));
        dto.setPa(safe(obstetric.getPa()));
        dto.setPs(safe(obstetric.getPs()));
        dto.setPv(safe(obstetric.getPv()));
        return dto;
    }


    private VitalsDTO mapVitals(Vitals vitals) {
        if (vitals == null) return new VitalsDTO();

        VitalsDTO dto = new VitalsDTO();
        dto.setPulse(safe(vitals.getPulse()));
        dto.setBp(safe(vitals.getBp()));
        dto.setSpo2(safe(vitals.getSpo2()));
        dto.setChest(safe(vitals.getChest()));
        dto.setPallor(safe(vitals.getPallor()));
        dto.setCovidVaccination(safe(vitals.getCovidVaccination()));
        return dto;
    }

    private List<TestDTO> mapTests(List<Test> tests) {
        if (tests == null) return List.of();

        return tests.stream()
                .map(t -> TestDTO.builder()
                        .id(safe(t.getId()))
                        .name(safe(t.getName()))
                        .amount(t.getAmount() == null ? 0 : t.getAmount())
                        .testParameterMasters(
                                t.getTestParameterMasters() == null ? List.of() : t.getTestParameterMasters()
                        )
                        .build()
                )
                .toList();
    }

    private List<PrescribedMedResponseDTO> mapPrescribedMeds(List<PrescribedMed> meds) {
        if (meds == null) return List.of();

        return meds.stream()
                .map(m -> {
                    PrescribedMedResponseDTO dto = new PrescribedMedResponseDTO();

                    dto.setMedicine(mapMedicine(m.getMedicineMaster()));
                    dto.setDose(safe(m.getDose()));
                    dto.setWhen(safe(m.getWhen()));
                    dto.setFrequency(safe(m.getFrequency()));
                    dto.setDuration(safe(m.getDuration()));
                    dto.setNotes(safe(m.getNotes()));

                    return dto;
                })
                .toList();
    }


    private MedicineMasterDTO mapMedicine(MedicineMaster med) {
        if (med == null) return new MedicineMasterDTO();

        MedicineMasterDTO dto = new MedicineMasterDTO();
        dto.setId(safe(med.getId()));
        dto.setName(safe(med.getName()));
        dto.setManufacturer(safe(med.getManufacturer()));
        return dto;
    }


    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }


}



