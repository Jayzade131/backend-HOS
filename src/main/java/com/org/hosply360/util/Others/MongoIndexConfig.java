package com.org.hosply360.util.Others;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.PartialIndexFilter;
import org.springframework.data.mongodb.core.query.Criteria;

@Configuration
@RequiredArgsConstructor
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        Index partialIndex = new Index()
                .on("org_id", Sort.Direction.ASC)
                .on("appointment_day", Sort.Direction.ASC)
                .on("token_number", Sort.Direction.ASC)
                .unique()
                .partial(PartialIndexFilter.of(Criteria.where("is_walkin").is(true)));

        mongoTemplate.indexOps(com.org.hosply360.dao.OPD.Appointment.class)
                .ensureIndex(partialIndex);
    }
}
