package com.org.hosply360.service.OPD.impl;


import com.org.hosply360.dao.OPD.OPDPaymentHistory;
import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryDTO;
import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryReqDTO;
import com.org.hosply360.repository.OPDRepo.OPDPaymentHistoryRepository;
import com.org.hosply360.service.OPD.OPDPaymentHistoryService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OPDPaymentHistoryServiceImpl implements OPDPaymentHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(OPDPaymentHistoryServiceImpl.class);
    private final OPDPaymentHistoryRepository paymentHistoryRepository;

    @Override
    public OPDPaymentHistoryDTO save(OPDPaymentHistoryReqDTO requestDto) {
        logger.info("Saving payment history for invoice: {}", requestDto.getInvoiceId());

        OPDPaymentHistory payment = ObjectMapperUtil.copyObject(requestDto, OPDPaymentHistory.class);
        payment.setDefunct(false);
        payment = paymentHistoryRepository.save(payment);

        return ObjectMapperUtil.copyObject(payment, OPDPaymentHistoryDTO.class);
    }

    @Override
    public List<OPDPaymentHistoryDTO> getByInvoiceId(String invoiceId) {
        logger.info("Fetching payment history for invoice: {}", invoiceId);

        List<OPDPaymentHistory> historyList = paymentHistoryRepository.findByInvoiceIdAndDefunctFalse(invoiceId);
        return historyList.stream()
                .map(entry -> ObjectMapperUtil.copyObject(entry, OPDPaymentHistoryDTO.class))
                .toList();
    }
}
