package com.org.hosply360.controller.IPD;



import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDDocumentRequestDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDDocumentController {

    private final IPDDocumentService ipdDocumentService;

    @PostMapping(EndpointConstants.IPD_DOCUMENT)
    public ResponseEntity<AppResponseDTO> createDocument(@RequestBody IPDDocumentRequestDTO requestDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDocumentService.createIPDDocument(requestDTO)));
    }

    @PutMapping(EndpointConstants.ID_DOCUMENT)
    public ResponseEntity<AppResponseDTO> updateDocument( @RequestBody IPDDocumentRequestDTO requestDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDocumentService.updateIPDDocument(requestDTO)));
    }

    @DeleteMapping(EndpointConstants.IPD_DOCUMENT_ID)
    public ResponseEntity<AppResponseDTO> deleteDocument(@PathVariable String id) {
        ipdDocumentService.deleteIPDDocument(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping(EndpointConstants.IPD_DOCUMENT_ID)
    public ResponseEntity<AppResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDocumentService.getIPDDocumentById(id)));
    }

    @GetMapping(EndpointConstants.IPD_ADMISSION_ID_DOCUMENT_ID)
    public ResponseEntity<AppResponseDTO> getDocumentByIpdAdmissionId(@PathVariable String ipdAdmissionId) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDocumentService.getByIpdAdmission(ipdAdmissionId)));
    }

    @GetMapping(EndpointConstants.IPD_DOC_ID)
    public ResponseEntity<AppResponseDTO> getDocumentById(@PathVariable String docId) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDocumentService.getDocumentPdfById(docId)));
    }
}
