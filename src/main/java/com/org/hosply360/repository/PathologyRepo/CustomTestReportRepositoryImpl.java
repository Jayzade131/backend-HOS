package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.dto.pathologyDTO.GetResTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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
public class CustomTestReportRepositoryImpl implements CustomTestReportRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PagedResultForTest<GetResTestReportDTO> findCustomTestReports(
            String orgId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String pId,
            String encryptedMobile,
            int page,
            int size
    ) {
        List<Criteria> baseCriteria = new ArrayList<>();
        baseCriteria.add(Criteria.where("organization.$id").is(new ObjectId(orgId)));
        baseCriteria.add(Criteria.where("defunct").is(false));

        if (startDateTime != null && endDateTime != null) {
            baseCriteria.add(Criteria.where("reportDate").gte(startDateTime).lte(endDateTime));
        }

        MatchOperation baseMatch = Aggregation.match(new Criteria().andOperator(baseCriteria.toArray(new Criteria[0])));

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

        LookupOperation lookupTest = LookupOperation.newLookup()
                .from("test_master")
                .localField("test.$id")
                .foreignField("_id")
                .as("testData");

        AggregationOperation lookupTestManager = context -> new org.bson.Document("$lookup",
                new org.bson.Document("from", "test_manager")
                        .append("let", new org.bson.Document("tmId", "$testManager.$id"))
                        .append("pipeline", List.of(
                                new org.bson.Document("$match",
                                        new org.bson.Document("$expr",
                                                new org.bson.Document("$eq", List.of("$_id", new org.bson.Document("$toObjectId", "$$tmId"))))
                                )
                        ))
                        .append("as", "testManagerData")
        );

        List<Criteria> patientCriteria = new ArrayList<>();
        if (StringUtils.hasText(pId)) {
            patientCriteria.add(Criteria.where("patientData.pId").is(pId));
        }
        if (StringUtils.hasText(encryptedMobile)) {
            patientCriteria.add(Criteria.where("patientData.contact_info.primary_phone").is(encryptedMobile));
        }

        MatchOperation patientMatch = null;
        if (!patientCriteria.isEmpty()) {
            patientMatch = Aggregation.match(new Criteria().andOperator(patientCriteria.toArray(new Criteria[0])));
        }

        ProjectionOperation project = Aggregation.project()
                .andExpression("toString($_id)").as("testReportId")
                .andExpression("toString($orgData._id)").as("orgId")
                .and("patientData.pId").as("pId")
                .andExpression("toString($patientData._id)").as("patId")
                .andExpression("{$toString: \"$testManager.$id\"}").as("testManagerId")
                .and("patientData.personal_info.first_name").as("firstName")
                .and("patientData.personal_info.last_name").as("lastName")
                .and("patientData.contact_info.primary_phone").as("phoneNo")
                .and("reportDate").as("testReportDateTime")
                .and("testData.test_name").as("testName");

        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "reportDate");
        SkipOperation skip = skip((long) Math.max(0, (page - 1) * size));
        LimitOperation limit = limit(size);

        List<AggregationOperation> pipeline = new ArrayList<>();
        pipeline.add(baseMatch);
        pipeline.add(lookupOrg);
        pipeline.add(unwindOrg);
        pipeline.add(lookupPatient);
        pipeline.add(unwindPatient);
        pipeline.add(lookupTest);
        pipeline.add(lookupTestManager);
        if (patientMatch != null) {
            pipeline.add(patientMatch);
        }
        pipeline.add(project);
        pipeline.add(sort);
        pipeline.add(skip);
        pipeline.add(limit);

        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        List<GetResTestReportDTO> results = mongoTemplate.aggregate(aggregation, "test_report", GetResTestReportDTO.class)
                .getMappedResults();

        List<AggregationOperation> countPipeline = new ArrayList<>();
        countPipeline.add(baseMatch);
        countPipeline.add(lookupPatient);
        countPipeline.add(unwindPatient);
        if (patientMatch != null) {
            countPipeline.add(patientMatch);
        }
        countPipeline.add(Aggregation.count().as("total"));

        Aggregation countAggregation = Aggregation.newAggregation(countPipeline);
        long total = mongoTemplate.aggregate(countAggregation, "test_report", org.bson.Document.class)
                .getUniqueMappedResult() != null
                ? mongoTemplate.aggregate(countAggregation, "test_report", org.bson.Document.class)
                .getUniqueMappedResult().getInteger("total")
                : 0;

        return new PagedResultForTest<>(results, total, page, size);
    }
}
