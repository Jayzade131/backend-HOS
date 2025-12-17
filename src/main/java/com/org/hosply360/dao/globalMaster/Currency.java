package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "currency_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency extends BaseModel {

    @Id
    private String id;
    @DBRef
    private Organization organization;

    @NotBlank(message = "Currency code is required")
    @Indexed(unique = true)
    @Field("currency_code")
    private String code;

    @NotBlank(message = "Primary symbol is required")
    @Field("symbol1")
    private List<String> symbol;

    @NotBlank(message = "Currency name is required")
    @Field("currency_name")
    private String name;

    @NotNull(message = "Decimal places are required")
    @Field("decimal_places")
    private long decimalPlaces;

    @Field("defunct")
    private Boolean defunct = false;
}

