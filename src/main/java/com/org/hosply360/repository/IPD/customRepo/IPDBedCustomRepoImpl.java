package com.org.hosply360.repository.IPD.customRepo;


import com.org.hosply360.constant.Enums.AdmitStatus;
import com.org.hosply360.dto.IPDDTO.BedDataDTO;
import com.org.hosply360.dto.IPDDTO.BedResponseDTO;
import com.org.hosply360.repository.IPD.IPDBedCustomRepo;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
public class IPDBedCustomRepoImpl implements IPDBedCustomRepo {

    private final MongoTemplate mongoTemplate;

    @Override
    public BedResponseDTO getBedsByWardId(String orgId, String wardId) {

        // Match beds for orgId and wardId that are not defunct
        MatchOperation matchBeds = match(Criteria.where("orgId").is(orgId)
                .and("ward_master.$id").is(new ObjectId(wardId))
                .and("defunct").is(false));

        // Lookup ward details
        LookupOperation lookupWard = Aggregation.lookup("wardMaster", "ward_master.$id", "_id", "wardData");

        // Lookup admissions for each bed
        LookupOperation lookupAdmissions = Aggregation.lookup("ipd_admissions", "_id", "bedMaster.$id", "admissions");

        // Filter only active admissions
        AddFieldsOperation filterActiveAdmission = addFields()
                .addFieldWithValue("activeAdmission",
                        ArrayOperators.Filter.filter("admissions")
                                .as("admission")
                                .by(context -> new Document("$and", List.of(
                                        new Document("$eq", List.of("$$admission.ipdStatus", "ADMITTED"))
                                )))
                ).build();

        // Lookup patient data for active admissions
        LookupOperation lookupPatients = Aggregation.lookup("patients",
                "activeAdmission.patient.$id", "_id", "patientData");

        // Project required fields and temporarily hold encrypted names
        ProjectionOperation project = project()
                .andExpression("ward_master.$id").as("wardId")
                .and(ArrayOperators.ArrayElemAt.arrayOf("wardData.wardName").elementAt(0)).as("wardName")
                .and("_id").as("bedId")
                .and("bedNo").as("bedNo")
                .and("status").as("status")

                // Add admitDateTime from activeAdmission
                .and(
                        ConditionalOperators.when(
                                        ComparisonOperators.Gt.valueOf(ArrayOperators.Size.lengthOfArray("activeAdmission"))
                                                .greaterThanValue(0)
                                )
                                .thenValueOf(
                                        AggregationExpression.from(
                                                MongoExpression.create("{ $arrayElemAt: ['$activeAdmission.admitDateTime', 0] }")
                                        )
                                )
                                .otherwiseValueOf(
                                        AggregationExpression.from(
                                                MongoExpression.create("{ $literal: null }")
                                        )
                                )
                ).as("admitDateTime")

                // Encrypted first name
                .and(
                        ConditionalOperators.when(
                                        ComparisonOperators.Gt.valueOf(ArrayOperators.Size.lengthOfArray("patientData"))
                                                .greaterThanValue(0)
                                )
                                .thenValueOf(AggregationExpression.from(
                                        MongoExpression.create("{ $arrayElemAt: ['$patientData.personal_info.first_name', 0] }")
                                ))
                                .otherwise("")
                ).as("encryptedFirstName")
                // Encrypted last name
                .and(
                        ConditionalOperators.when(
                                        ComparisonOperators.Gt.valueOf(ArrayOperators.Size.lengthOfArray("patientData"))
                                                .greaterThanValue(0)
                                )
                                .thenValueOf(AggregationExpression.from(
                                        MongoExpression.create("{ $arrayElemAt: ['$patientData.personal_info.last_name', 0] }")
                                ))
                                .otherwise("")
                ).as("encryptedLastName");

        // Sort by status (AVAILABLE first) and then by bedNo
        SortOperation sortOperation = sort(Sort.by(Sort.Order.asc("status"), Sort.Order.asc("bedNo")));

        Aggregation aggregation = newAggregation(
                matchBeds,
                lookupWard,
                lookupAdmissions,
                filterActiveAdmission,
                lookupPatients,
                project,
                sortOperation
        );

        // Execute aggregation
        List<BedDataDTO> results = mongoTemplate.aggregate(aggregation, "wardBedMaster", BedDataDTO.class)
                .getMappedResults();

        // Decrypt patient names
        results.forEach(bed -> {
            String firstName = bed.getEncryptedFirstName();
            String lastName = bed.getEncryptedLastName();

            String decryptedName = "";
            if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())) {
                decryptedName = (firstName != null ? EncryptionUtil.decrypt(firstName) : "")
                        + " "
                        + (lastName != null ? EncryptionUtil.decrypt(lastName) : "");
            }
            bed.setPatientName(decryptedName.trim());
            bed.setEncryptedFirstName(null);
            bed.setEncryptedLastName(null);
        });

        // Calculate counts
        long totalBeds = results.size();
        long totalAvailableBeds = results.stream()
                .filter(b -> b.getStatus() == AdmitStatus.AVAILABLE)
                .count();
        long totalBookedBeds = totalBeds - totalAvailableBeds;

        // Build and return response
        return BedResponseDTO.builder()
                .beds(results)
                .totalBeds(totalBeds)
                .totalAvailableBeds(totalAvailableBeds)
                .totalBookedBeds(totalBookedBeds)
                .build();
    }
}
