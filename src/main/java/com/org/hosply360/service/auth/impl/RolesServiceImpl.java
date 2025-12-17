package com.org.hosply360.service.auth.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Roles;
import com.org.hosply360.dto.authDTO.RolesDTO;
import com.org.hosply360.dto.authDTO.RolesResDto;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.exception.UserException;
import com.org.hosply360.repository.authRepo.RolesRepository;
import com.org.hosply360.service.auth.RolesService;
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
public class RolesServiceImpl implements RolesService {

    private static final Logger logger = LoggerFactory.getLogger(RolesServiceImpl.class);
    private final RolesRepository rolesRepository;

    public static RolesDTO getRolesDTO(Roles roles) {
        return ObjectMapperUtil.copyObject(roles, RolesDTO.class);
    }

    public void validateUniqueName(String name) {
        if (rolesRepository.findByName(name).isPresent()) {
            logger.info("Role name {} already exists", name);
            throw new GlobalMasterException(ErrorConstant.ROLE_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public RolesResDto createRole(RolesDTO dto) {
        if (dto == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        validateUniqueName(dto.getName());

        Roles entityRoles = ObjectMapperUtil.copyObject(dto, Roles.class);
        entityRoles.setDefunct(false);
        Roles savedRoles = rolesRepository.save(entityRoles);
        logger.info("Role created successfully");


        return ObjectMapperUtil.copyObject(savedRoles, RolesResDto.class);

    }

    @Override
    @Transactional
    public RolesDTO updateRole(RolesDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Roles existing = rolesRepository.findByIdAndDefunct(dto.getId(), false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Optional<Roles> existingByName = rolesRepository.findByName(dto.getName());

        if (existingByName.isPresent() && !existingByName.get().getId().equals(dto.getId())) {
            logger.info("Role name {} already exists", dto.getName());
            throw new UserException(ErrorConstant.ROLE_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("id"));
        Roles updated = rolesRepository.save(existing);
        logger.info("Role updated successfully");
        return getRolesDTO(updated);
    }

    @Override
    public RolesDTO getRole(String id) {
        Roles entity = rolesRepository.findByIdAndDefunct(id, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return getRolesDTO(entity);
    }

    @Override
    public List<RolesDTO> getAllRoles(int page, int size) {
        logger.info("Fetching all roles");
        Pageable pageable = PageRequest.of(page, size);
        Page<Roles> pageResult = rolesRepository.findAllByDefunct(false, pageable);
        return pageResult.stream().map(RolesServiceImpl::getRolesDTO).toList();
    }

    @Override
    public void deleteRole(String id) {
        Roles roles = rolesRepository.findByIdAndDefunct(id, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting role with ID: {}", id);
        roles.setDefunct(true);
        rolesRepository.save(roles);
        logger.info("Deleted role with ID: {}", id);
    }
}
