package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document("tariff")
public class Tariff extends BaseModel {

    @Id
    private String id;

    @DBRef
    @Field("organization")
    private Organization organization;

    @Field("name")
    private String name;

    @Field("defunct")
    private boolean defunct;
}
