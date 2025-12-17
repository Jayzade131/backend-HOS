package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dto.pathologyDTO.GetResTestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@Repository
@RequiredArgsConstructor
public class CustomTestManagerRepositoryImpl implements CustomTestManagerRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PagedResultForTest<GetResTestManagerDTO> findCustomTestManagersDynamic(
            String orgId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String pId,
            String encryptedMobile,
            List<TestStatus> statuses,
            int page,
            int size
    ) {
        // Build criteria (these are evaluated later in the aggregation after lookups/unwinds)
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("organization.$id").is(new ObjectId(orgId)));
        criteriaList.add(Criteria.where("defunct").is(false));
        criteriaList.add(Criteria.where("testDateTime").gte(fromDate).lte(toDate));

        if (StringUtils.hasText(pId)) {
            // This relies on doing patient lookup/unwind BEFORE the match stage
            criteriaList.add(Criteria.where("patientData.pId").is(pId));
        }
        if (StringUtils.hasText(encryptedMobile)) {
            criteriaList.add(Criteria.where("patientData.contact_info.primary_phone").is(encryptedMobile));
        }
        if (statuses != null && !statuses.isEmpty()) {
            criteriaList.add(Criteria.where("status").in(statuses));
        }

        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        MatchOperation match = match(criteria);

        // lookups & unwinds
        LookupOperation lookupOrg = LookupOperation.newLookup()
                .from("organization_master")
                .localField("organization.$id")
                .foreignField("_id")
                .as("orgData");
        UnwindOperation unwindOrg = unwind("orgData", true);

        LookupOperation lookupPatient = LookupOperation.newLookup()
                .from("patients")
                .localField("patient.$id")
                .foreignField("_id")
                .as("patientData");
        UnwindOperation unwindPatient = unwind("patientData", true);

        LookupOperation lookupTest = LookupOperation.newLookup()
                .from("test_master")
                .localField("test.$id")
                .foreignField("_id")
                .as("testData");

        LookupOperation lookupPackage = LookupOperation.newLookup()
                .from("package_master")
                .localField("packageE.$id")
                .foreignField("_id")
                .as("packageData");
        UnwindOperation unwindPackage = unwind("packageData", true);

        LookupOperation lookupPackageTestReport = LookupOperation.newLookup()
                .from("package_test_report")
                .localField("_id")
                .foreignField("testManager.$id") // matches @DBRef testManager in PackageTestReport
                .as("packageTestReportData");

        UnwindOperation unwindPackageTestReport = unwind("packageTestReportData", true);

        // projection (same as your previous projection)
        ProjectionOperation project = project()
                .andExpression("toString($_id)").as("id")
                .andExpression("toString($orgData._id)").as("orgId")
                .andExpression("toString($packageTestReportData._id)").as("packageTestReportId")

                .and("patientData.personal_info.first_name").as("firstName")
                .and("patientData.personal_info.last_name").as("lastName")
                .and("patientData.pId").as("pId")

                .andExpression("toString($patientData._id)").as("patId")
                .and("patientData.contact_info.primary_phone").as("phoneNo")
                .and("status").as("status")
                .and("testDateTime").as("testDateTime")
                .and("totalAmount").as("totalAmount")
                .and("source").as("source")
                .and("hasPaid").as("hasPaid")
                .and("paidAmount").as("paidAmount")
                .andExpression("totalAmount - paidAmount").as("balanceAmount")
                .andExpression("{ $map: { input: '$testData', as: 'td', in: { testID: { $toString: '$$td._id' }, testName: '$$td.test_name' } } }")
                .as("testDto")
                .and("packageData.package_name").as("packageName");



        SortOperation sort = sort(Sort.Direction.DESC, "testDateTime");
        SkipOperation skip = skip((long) Math.max(0, (page - 1) * size));
        LimitOperation limit = limit(size);





        // Build aggregation for paged data (NOTE: lookup/unwind of patient is done BEFORE match so we can match on patientData fields)
        List<AggregationOperation> dataOps = new ArrayList<>();
        dataOps.add(lookupOrg);
        dataOps.add(unwindOrg);
        dataOps.add(lookupPatient);
        dataOps.add(unwindPatient);
        dataOps.add(match);
        dataOps.add(lookupTest);
        dataOps.add(lookupPackage);
        dataOps.add(unwindPackage);
        dataOps.add(lookupPackageTestReport);
        dataOps.add(unwindPackageTestReport);
        dataOps.add(project);
        dataOps.add(sort);
        dataOps.add(skip);
        dataOps.add(limit);

        Aggregation dataAggregation = newAggregation(dataOps);

        List<GetResTestManagerDTO> results = mongoTemplate
                .aggregate(dataAggregation, "test_manager", GetResTestManagerDTO.class)
                .getMappedResults();

        List<AggregationOperation> countOps = new ArrayList<>();
        countOps.add(lookupOrg);
        countOps.add(unwindOrg);
        countOps.add(lookupPatient);
        countOps.add(unwindPatient);
        countOps.add(match);
        countOps.add(lookupTest);
        countOps.add(lookupPackage);
        countOps.add(unwindPackage);
        countOps.add(lookupPackageTestReport);
        countOps.add(unwindPackageTestReport);
        countOps.add(count().as("total"));

        Aggregation countAggregation = newAggregation(countOps);
        AggregationResults<Document> countAggResults = mongoTemplate.aggregate(countAggregation, "test_manager", Document.class);

        long total = 0;
        if (!countAggResults.getMappedResults().isEmpty()) {
            Object t = countAggResults.getMappedResults().get(0).get("total");
            if (t instanceof Number) {
                total = ((Number) t).longValue();
            } else {
                total = Long.parseLong(String.valueOf(t));
            }
        }

        return new PagedResultForTest<>(results, total, page, size);
    }
}
