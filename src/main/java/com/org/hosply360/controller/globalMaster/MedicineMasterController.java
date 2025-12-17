package com.org.hosply360.controller.globalMaster;


import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.MedicineCsvRowDto;
import com.org.hosply360.service.globalMaster.MedicineMasterService;
import com.org.hosply360.util.csv.CsvHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class MedicineMasterController {

    private static final Logger logger = LoggerFactory.getLogger(MedicineMasterController.class);

    private final MedicineMasterService medicineMasterService;
    @PostMapping(value = EndpointConstants.UPLOAD_MEDICINE_CSV, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AppResponseDTO> uploadMedicineCsv(@RequestParam("file") MultipartFile file) {
        logger.info("Uploading medicine CSV");

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest()
                    .body(AppResponseDTO.badRequest("Invalid file type. Only CSV files are allowed."));
        }

        try {
            List<MedicineCsvRowDto> csvRows = CsvHelper.parseCsv(file.getInputStream(), record ->
                    new MedicineCsvRowDto(
                            record.get("name"),
                            record.get("manufacturer"),
                            Boolean.parseBoolean(record.get("is_defunct"))
                    )
            );

            medicineMasterService.saveFromCsv(csvRows);
            return ResponseEntity.ok(AppResponseDTO.ok("Medicines uploaded successfully"));
        } catch (Exception e) {
            logger.error("Error while uploading CSV: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(AppResponseDTO.internalServerError("Something went wrong while processing the file."));
        }
    }

    @GetMapping(EndpointConstants.GET_ALL_MEDICINES_API)
    public ResponseEntity<AppResponseDTO> getAllMedicines() {
        logger.info("Fetching all medicines");
        return ResponseEntity.ok(AppResponseDTO.ok(medicineMasterService.findAll()));
    }

}
