package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IpdTransferDTO;
import com.org.hosply360.dto.IPDDTO.IpdTransferHistoryDto;
import com.org.hosply360.dto.IPDDTO.TransferReceiptPdfDTO;

import java.util.List;

public interface IPDTransferService {
    String createIpdTransfer(IpdTransferDTO requestDTO);

    List<IpdTransferHistoryDto> getTransfersByIpdAdmission(String ipdAdmissionId);

    TransferReceiptPdfDTO getTransferReceiptPdf(String transferId);

}
