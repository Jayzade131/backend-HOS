package com.org.hosply360.service.auth.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Access;
import com.org.hosply360.dto.authDTO.AccessDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.authRepo.AccessRepository;
import com.org.hosply360.service.auth.AccessService;
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
public class AccessServiceImpl implements AccessService {

    private static final Logger logger = LoggerFactory.getLogger(AccessServiceImpl.class);

    private final AccessRepository accessRepository;


    public static AccessDTO getAccessDTO(Access access) {
        return ObjectMapperUtil.copyObject(access, AccessDTO.class);
    }

    private void validateUniqueAccessName(String accessName) {
        if (accessRepository.findByAccessName(accessName).isPresent()) {
            logger.info("Access name {} already exists", accessName);
            throw new GlobalMasterException(ErrorConstant.ACCESS_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional
    public AccessDTO createAccess(AccessDTO dto) {
        if (dto == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        validateUniqueAccessName(dto.getAccessName());
        Access entity = ObjectMapperUtil.copyObject(dto, Access.class);
        Access saved = accessRepository.save(entity);
        logger.info("Access created successfully");
        return getAccessDTO(saved);
    }

    @Transactional
    @Override
    public AccessDTO updateAccess(AccessDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Access existingAccess = accessRepository.findById(dto.getId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ACCESS_NOT_FOUND, HttpStatus.NOT_FOUND));

        Optional<Access> existingAccessByName = accessRepository.findByAccessName(dto.getAccessName());
        if (existingAccessByName.isPresent() && !existingAccessByName.get().getId().equals(existingAccess.getId())) {
            logger.info("Access name {} already exists", dto.getAccessName());
            throw new GlobalMasterException(ErrorConstant.ACCESS_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existingAccess, List.of("accessId"));

        Access updatedAccess = accessRepository.save(existingAccess);
        logger.info("Access updated successfully");
        return getAccessDTO(updatedAccess);
    }

    @Override
    public AccessDTO getAccess(String id) {
        Access entity = accessRepository.findByIdInAndDefunct(id, false);
        return getAccessDTO(entity);
    }

    @Override
    public List<AccessDTO> getAllAccess(int page, int size) {
        logger.info("Fetched all accesses successfully");
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Access> pageResult = accessRepository.findAllByDefunct(false, pageable);
        return pageResult.stream().map(AccessServiceImpl::getAccessDTO).toList();
    }


    @Override
    public void deleteAccess(String id) {
        Access access = accessRepository.findById(id)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ACCESS_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting access with id: {}", id);
        access.setDefunct(true);
        accessRepository.save(access);
        logger.info("Access deleted successfully");
    }


}
