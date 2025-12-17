    package com.org.hosply360.dao.globalMaster;

    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.Field;

    import javax.validation.constraints.NotBlank;

    @Getter
    @Setter
    @NoArgsConstructor
    @Document("medicine_master")
    public class MedicineMaster {

        @Id
        private String id;

        @NotBlank
        @Field("name")

        private String name;

        @Field("manufacturer")
        private String manufacturer;

        @Field("is_defunct")
        private Boolean isDefunct = false;
    }
