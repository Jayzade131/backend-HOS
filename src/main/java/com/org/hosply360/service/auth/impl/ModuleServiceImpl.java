package com.org.hosply360.service.auth.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Modules;
import com.org.hosply360.dto.authDTO.ModuleDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.authRepo.ModuleRepository;
import com.org.hosply360.service.auth.ModuleService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ModuleServiceImpl implements ModuleService {

    private static final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    private final ModuleRepository moduleRepository;

    public static ModuleDTO getModuleDTO(Modules modules) {
        return ObjectMapperUtil.copyObject(modules, ModuleDTO.class);

    }

    private void validateUniqueModuleName(String moduleName) {
        if (moduleRepository.findByModuleName(moduleName).isPresent()) {
            logger.info("Module name {} already exists", moduleName);
            throw new GlobalMasterException(ErrorConstant.MODULE_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public ModuleDTO createModule(ModuleDTO dto) {
        if (dto == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        validateUniqueModuleName(dto.getModuleName());
        Modules entity = ObjectMapperUtil.copyObject(dto, Modules.class);
        Modules saved = moduleRepository.save(entity);
        logger.info("Module created successfully");
        return getModuleDTO(saved);
    }


    @Override
    @Transactional
    public ModuleDTO updateModule(ModuleDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Modules existing = moduleRepository.findByIdAndDefunct(dto.getId(), false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Optional<Modules> existingByName = moduleRepository.findByModuleName(dto.getModuleName());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(dto.getId())) {
            logger.info("Module name {} already exists", existingByName.get().getModuleName());
            throw new GlobalMasterException(ErrorConstant.MODULE_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("moduleId"));

        Modules updated = moduleRepository.save(existing);
        logger.info("Module updated successfully");
        return getModuleDTO(updated);
    }


    @Override
    public ModuleDTO getModule(String id) {
        Modules entity = moduleRepository.findByIdAndDefunct(id, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return getModuleDTO(entity);
    }

    @Override
    public List<ModuleDTO> getAllModule(int page, int size) {
        logger.info("Fetching all Module");
        Pageable pageable = PageRequest.of(page, size);
        Page<Modules> pageResult = moduleRepository.findAllByDefunct(false, pageable);
        return pageResult.stream().map(ModuleServiceImpl::getModuleDTO).toList();
    }

    @Override
    public void deleteModule(String id) {
        Modules modules = moduleRepository.findById(id)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting module  with ID: {}", id);
        modules.setDefunct(true);
        moduleRepository.save(modules);
        logger.info("Deleted module  with ID: {}", id);
    }
}
