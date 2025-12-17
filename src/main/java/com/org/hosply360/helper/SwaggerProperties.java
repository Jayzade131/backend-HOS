package com.org.hosply360.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "swagger")
@Setter
@Getter
public class SwaggerProperties {

    private String title;
    private String version;
    private String description;
}
