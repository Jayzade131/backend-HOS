package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IpdTransferDTO;
import com.org.hosply360.dto.IPDDTO.IpdTransferHistoryDto;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;

import java.util.List;

public interface IPDTransferService {
    String createIpdTransfer(IpdTransferDTO requestDTO);

    List<IpdTransferHistoryDto> getTransfersByIpdAdmission(String ipdAdmissionId);

    PdfResponseDTO generateTransferReceiptPdf(String transferId);
}
