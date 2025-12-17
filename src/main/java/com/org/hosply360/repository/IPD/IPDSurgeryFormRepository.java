package com.org.hosply360.repository.IPD;


import com.org.hosply360.dao.IPD.IPDSurgery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IPDSurgeryFormRepository extends MongoRepository<IPDSurgery, String> {



        List<IPDSurgery> findByOrgIdAndDefunctFalse(String orgId);

        List<IPDSurgery> findByOrgIdAndIpdAdmissionIdAndDefunctFalse(String orgId, String ipdAdmissionId);

}
