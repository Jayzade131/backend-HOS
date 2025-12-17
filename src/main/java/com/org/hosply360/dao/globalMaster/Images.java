package com.org.hosply360.dao.globalMaster;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Images {

    @Id
    private String id;

    private String name;

    private byte[] image;
}
