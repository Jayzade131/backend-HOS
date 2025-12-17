package com.org.hosply360.dao.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("access")
public class Access {

    @Id
    private String id;

    @Field("access_name")
    private String accessName;

    @Field("defunct")
    private boolean defunct;

}
