package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.DietPlanPdfResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDDeitDTO;
import com.org.hosply360.dto.IPDDTO.IPDDietReqDTO;

import java.util.List;

public interface IPDDietService {

    String createDiet(IPDDietReqDTO requestDTO);

    String updateDiet(IPDDietReqDTO requestDTO);

    IPDDeitDTO getDietById(String id);

    void deleteDiet(String id);

    List<IPDDeitDTO> getAllDiet(String ipdAdmissionId);

    DietPlanPdfResponseDTO getDietPlanPdf(String ipdAdmissionId);
}
