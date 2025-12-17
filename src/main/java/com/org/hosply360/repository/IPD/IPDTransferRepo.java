package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IpdTransfer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPDTransferRepo extends MongoRepository<IpdTransfer, String> {

    List<IpdTransfer> findByIpdAdmission_IdAndDefunctFalse(String ipdAdmissionId);

}
