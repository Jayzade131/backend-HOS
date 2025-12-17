package com.org.hosply360.repository.PathologyRepo;
import com.org.hosply360.dto.pathologyDTO.GetResPacTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

@Repository
@RequiredArgsConstructor
public class CustomPacTestReportRepositoryImpl implements CustomPacTestReportRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PagedResultForTest<GetResPacTestReportDTO> findCustomPackageTestReports(
            String orgId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String pId,
            String encryptedMobile,
            int page,
            int size
    ) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("organization.$id").is(new ObjectId(orgId)));
        criteriaList.add(Criteria.where("defunct").is(false));

        if (startDateTime != null && endDateTime != null) {
            criteriaList.add(Criteria.where("reportDate").gte(startDateTime).lte(endDateTime));
        }
        if (StringUtils.hasText(pId)) {
            criteriaList.add(Criteria.where("patientData.pId").is(pId));
        }
        if (StringUtils.hasText(encryptedMobile)) {
            criteriaList.add(Criteria.where("patientData.contact_info.primary_phone").is(encryptedMobile));
        }

        MatchOperation match = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        LookupOperation lookupOrg = LookupOperation.newLookup()
                .from("organization_master")
                .localField("organization.$id")
                .foreignField("_id")
                .as("orgData");

        UnwindOperation unwindOrg = Aggregation.unwind("orgData", true);

        LookupOperation lookupPatient = LookupOperation.newLookup()
                .from("patients")
                .localField("patient.$id")
                .foreignField("_id")
                .as("patientData");

        UnwindOperation unwindPatient = Aggregation.unwind("patientData", true);

        LookupOperation lookupPackage = LookupOperation.newLookup()
                .from("package_master")
                .localField("packageE.$id")
                .foreignField("_id")
                .as("packageData");

        UnwindOperation unwindPackage = Aggregation.unwind("packageData", true);
        UnwindOperation unwindPackageItem = Aggregation.unwind("packageTestReportItem", true);

        LookupOperation lookupTest = LookupOperation.newLookup()
                .from("test_master")
                .localField("packageTestReportItem.test.$id")
                .foreignField("_id")
                .as("testLookup");

        GroupOperation group = Aggregation.group("$_id")
                .first("orgData").as("orgData")
                .first("patientData").as("patientData")
                .first("testManager").as("testManager")
                .first("packageData.package_name").as("packageName")
                .first("status").as("status")
                .first("reportDate").as("reportDate")
                .addToSet(ArrayOperators.ArrayElemAt.arrayOf("testLookup.test_name").elementAt(0)).as("testNames");

        ProjectionOperation project = Aggregation.project()
                .andExpression("$_id").as("packageTestReportId")
                .and("orgData._id").as("orgId")
                .and("patientData.pId").as("pId")
                .and("patientData._id").as("patId")
                .and("packageName").as("packageName")
                .andExpression("{$toString: \"$testManager.$id\"}").as("testManagerId")
                .and("patientData.personal_info.first_name").as("firstName")
                .and("patientData.personal_info.last_name").as("lastName")
                .and("patientData.contact_info.primary_phone").as("phoneNo")
                .and("status").as("status")
                .and("reportDate").as("testReportDateTime")
                .and("testNames").as("testNames");

        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "testReportDateTime");
        SkipOperation skip = skip((long) Math.max(0, (page - 1) * size));
        LimitOperation limit = limit(size);
        List<AggregationOperation> pipeline = new ArrayList<>();
        pipeline.add(lookupOrg);
        pipeline.add(unwindOrg);
        pipeline.add(lookupPatient);
        pipeline.add(unwindPatient);
        pipeline.add(lookupPackage);
        pipeline.add(unwindPackage);
        pipeline.add(unwindPackageItem);
        pipeline.add(lookupTest);
        pipeline.add(match);
        pipeline.add(group);
        pipeline.add(project);
        pipeline.add(sort);
        pipeline.add(skip);
        pipeline.add(limit);

        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        List<GetResPacTestReportDTO> results = mongoTemplate.aggregate(aggregation, "package_test_report", GetResPacTestReportDTO.class)
                .getMappedResults();

        List<AggregationOperation> countPipeline = new ArrayList<>();
        countPipeline.add(match);
        countPipeline.add(Aggregation.count().as("total"));

        Aggregation countAggregation = Aggregation.newAggregation(countPipeline);
        Integer total = mongoTemplate.aggregate(countAggregation, "package_test_report", org.bson.Document.class)
                .getUniqueMappedResult() != null
                ? mongoTemplate.aggregate(countAggregation, "package_test_report", org.bson.Document.class).getUniqueMappedResult().getInteger("total")
                : 0;

        return new PagedResultForTest<>(results, total, page, size);
    }

}
