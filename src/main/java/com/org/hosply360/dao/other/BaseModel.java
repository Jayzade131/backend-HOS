package com.org.hosply360.dao.other;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BaseModel {

    @NotNull
    @Field("created_date")
    private LocalDateTime createdDate;

    @NotNull
    @Field("created_by")
    private String CreatedBy;

    @Field("updated_by")
    private String UpdatedBy;

    @Field("updated_date")
    private LocalDateTime updatedDate;
}
