package com.org.hosply360.service.auth;

import com.org.hosply360.dto.authDTO.ModuleDTO;

import java.util.List;

public interface ModuleService {
    ModuleDTO createModule(ModuleDTO moduleDto);

    ModuleDTO updateModule(ModuleDTO moduleDto);

    ModuleDTO getModule(String id);

    List<ModuleDTO> getAllModule(int page, int size);

    void deleteModule(String id);

}
