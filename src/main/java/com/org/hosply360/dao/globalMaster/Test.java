package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "test_master")
public class Test extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("test_name")
    private String name;
    @Field("test_amount")
    private Long amount;
    @Field("test_parameter_masters")
    private List<TestParameterMaster> testParameterMasters;
    @Field("defunct")
    private boolean defunct;

}
