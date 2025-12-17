    package com.org.hosply360.dao.OPD;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.springframework.data.mongodb.core.mapping.Field;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class Vitals {

        @Field("blood_pressure")
        private String bp;

        @Field("pulse")
        private String pulse;

        @Field("spo2")
        private String spo2;

        @Field("covid_vaccination")
        private String covidVaccination;

        @Field("pallor")
        private String pallor;

        @Field("chest")
        private String chest;

    }
