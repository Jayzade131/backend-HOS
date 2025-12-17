package com.org.hosply360.service.auth;

import com.org.hosply360.dto.authDTO.AccessDTO;

import java.util.List;

public interface AccessService {

    AccessDTO createAccess(AccessDTO accessDto);

    AccessDTO updateAccess(AccessDTO accessDto);

    AccessDTO getAccess(String id);

    List<AccessDTO> getAllAccess(int page, int size);

    void deleteAccess(String id);
}
