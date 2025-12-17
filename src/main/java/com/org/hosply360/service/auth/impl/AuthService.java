package com.org.hosply360.service.auth.impl;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.Enums.RoleEnum;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Access;
import com.org.hosply360.dao.auth.ModuleAccessMapping;
import com.org.hosply360.dao.auth.Modules;
import com.org.hosply360.dao.auth.RoleModuleMapping;
import com.org.hosply360.dao.auth.Roles;
import com.org.hosply360.dao.auth.Users;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.authDTO.AccessDTO;
import com.org.hosply360.dto.authDTO.AuthRequestDTO;
import com.org.hosply360.dto.authDTO.JwtResponseDTO;
import com.org.hosply360.dto.authDTO.ModuleAccessDto;
import com.org.hosply360.dto.authDTO.PasswordReqDTO;
import com.org.hosply360.dto.authDTO.RoleModuleAccessDto;
import com.org.hosply360.dto.authDTO.UserRegisterReqDto;
import com.org.hosply360.dto.authDTO.UserResponseDTO;
import com.org.hosply360.dto.authDTO.UserUpdateReqDTO;
import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.UserException;
import com.org.hosply360.helper.CustomUserDetails;
import com.org.hosply360.repository.authRepo.ModuleAccessRepository;
import com.org.hosply360.repository.authRepo.RoleModuleRepository;
import com.org.hosply360.repository.authRepo.RolesRepository;
import com.org.hosply360.repository.authRepo.UsersRepository;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.util.Others.PasswordGenerator;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private DoctorMasterRepository doctorMasterRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private OrganizationMasterRepository organizationMasterRepository;

    @Autowired
    private ModuleAccessRepository moduleAccessRepository;

    @Autowired
    private RoleModuleRepository roleModuleRepository;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email}")
    private String appEmail;

    private UserResponseDTO entyToDto(Users user) {
        UserResponseDTO userResponseDTO = ObjectMapperUtil.copyObject(user, UserResponseDTO.class);

        userResponseDTO.setOrganizations(user.getOrganizations().stream().map(org -> {
            var orgDto = ObjectMapperUtil.copyObject(org, OrganizationDTO.class);
            orgDto.setAddress(ObjectMapperUtil.copyObject(org.getAddress(), AddressDTO.class));
            return orgDto;
        }).toList());

        List<RoleModuleAccessDto> collectRoleModuleAccessDto = user.getRoles().stream().map(roles -> {
            List<Modules> collectModules = roleModuleRepository.findAllByRoles_Id(roles.getId()).stream().map(RoleModuleMapping::getModules).collect(Collectors.toList());
            List<ModuleAccessDto> collectModuleAccessDto = collectModules.stream().map(module -> {
                List<Access> collectAccess = moduleAccessRepository.findAllByModules_Id(module.getId()).stream().map(ModuleAccessMapping::getAccess).collect(Collectors.toList());
                List<AccessDTO> accessDTOS = ObjectMapperUtil.copyListObject(collectAccess, AccessDTO.class);

                ModuleAccessDto moduleAccessDto = new ModuleAccessDto();
                moduleAccessDto.setId(module.getId());
                moduleAccessDto.setModuleName(module.getModuleName());
                moduleAccessDto.setDefunct(module.getDefunct());
                moduleAccessDto.setAccessDTO(accessDTOS);

                return moduleAccessDto;

            }).collect(Collectors.toList());

            RoleModuleAccessDto roleModuleAccessDto = new RoleModuleAccessDto();
            roleModuleAccessDto.setId(roles.getId());
            roleModuleAccessDto.setName(roles.getName());
            roleModuleAccessDto.setDescription(roles.getDescription());
            roleModuleAccessDto.setDefunct(roles.isDefunct());
            roleModuleAccessDto.setModuleAccessDto(collectModuleAccessDto);
            return roleModuleAccessDto;

        }).collect(Collectors.toList());

        userResponseDTO.setRoleModuleAccessDtos(collectRoleModuleAccessDto);
        userResponseDTO.setDefaultPassword(user.isDefaultPassword());

        return userResponseDTO;
    }

    public JwtResponseDTO authenticate(AuthRequestDTO authRequestDTO) {

        if (authRequestDTO == null || (authRequestDTO.getUsername() == null && authRequestDTO.getPassword() == null)) {
            logger.info("Invalid request data");
            throw new UserException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));

        if (!authentication.isAuthenticated()) {
            logger.warn("Authentication failed for username: {}", authRequestDTO.getUsername());
            throw new UserException(ErrorConstant.INVALID_AUTHENTICATION, HttpStatus.BAD_REQUEST);
        }

        Users user = usersRepository.findByUsername(authRequestDTO.getUsername()).orElseThrow(() -> new UserException(ErrorConstant.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
        if (user.isDefaultPassword()) {
            jwtResponseDTO.setMessage(ApplicationConstant.JWT_RESPONSE_MSG + user.getUsername());
            logger.info("Change Default Password : {}", user.getUsername());
        } else {
            jwtResponseDTO.setMessage(ApplicationConstant.SUCCESS);
        }

        List<String> roleNames = user.getRoles().stream().map(Roles::getName).toList();

        String token = jwtService.generateToken(user, user.getRoles());

        logger.info("User authenticated successfully with username: {}", user.getUsername());

        UserResponseDTO userResponseDTO = entyToDto(user);
        jwtResponseDTO.setUserResponseDto(userResponseDTO);
        jwtResponseDTO.setToken(token);
        return jwtResponseDTO;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRegisterReqDto userRegisterReqDto) {

        if (userRegisterReqDto == null || (userRegisterReqDto.getUsername() == null && userRegisterReqDto.getEmail() == null && userRegisterReqDto.getMobileNo() == null)) {
            logger.info("Invalid request data");
            throw new UserException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        List<Roles> rolesList = rolesRepository.findAllByIDAndDefunct(userRegisterReqDto.getRoleId(), false).orElseThrow(() -> new UserException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST));

        List<Organization> organizationList = organizationMasterRepository.findAllByIDAndDefunct(userRegisterReqDto.getOrganizationId(), false).orElseThrow(() -> new UserException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.BAD_REQUEST));

        Users user = ObjectMapperUtil.copyObject(userRegisterReqDto, Users.class);
        String generatePassword = passwordGenerator.generatePassword(8);
        logger.info("default password : {}", generatePassword);
        user.setPassword(passwordEncoder.encode(generatePassword));
        user.setRoles(rolesList);
        user.setDefaultPassword(true);
        user.setOrganizations(organizationList);
        user.setDefunct(false);
        Users savedUsers = usersRepository.save(user);
        getUserCredOnEmail(savedUsers, generatePassword);

        logger.info("User registered with username: {}", userRegisterReqDto.getUsername());

        return entyToDto(savedUsers);
    }


    public boolean checkUsernameExists(String username) {
        return usersRepository.findByUsernameIgnoreCase(username).isPresent();
    }


    public boolean checkMobileExists(String mobileNo) {
        return usersRepository.findByMobileNo(mobileNo).isPresent();
    }

    public boolean checkEmail(String email) {
        return usersRepository.findByEmailIgnoreCase(email).isPresent();
    }

    public List<UserResponseDTO> getAllUsersByOrganizationAndDefunct(String organizationId) {
        return usersRepository.findAllByOrganizations_IdAndDefunct(organizationId, false)
                .stream().sorted(Comparator.comparing(Users::getCreatedDate).reversed())
                .map(this::entyToDto)
                .toList();

    }

    public List<UserResponseDTO> getAllDoctorUsers(String organizationId) {
        return usersRepository.findAllByOrganizations_IdAndDefunct(organizationId, false)
                .stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> RoleEnum.DOCTOR.name().equalsIgnoreCase(role.getName())))
                .filter(user -> !doctorMasterRepository.existsByUserIdAndOrganizationIdAndDefunct(user.getId(), organizationId, false))
                .sorted(Comparator.comparing(Users::getCreatedDate).reversed())
                .map(this::entyToDto)
                .toList();
    }


    public void changeNewPasswordUsingDefaultPassword(PasswordReqDTO passwordReqDTO) {
        Users user = usersRepository.findById(passwordReqDTO.getUserId()).orElseThrow(() -> new UserException(ErrorConstant.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (!user.isDefaultPassword()) {
            logger.info("User with username: {} does not have a default password", user.getUsername());
            throw new UserException(ErrorConstant.INVALID_AUTHENTICATION, HttpStatus.BAD_REQUEST);
        }

        if (!passwordReqDTO.getNewPassword().equals(passwordReqDTO.getConfirmPassword())) {
            logger.info("New password and confirm password do not match for username: {}", user.getUsername());
            throw new UserException(ErrorConstant.PASSWORD_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        if (this.passwordEncoder.matches(passwordReqDTO.getDefaultPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordReqDTO.getNewPassword()));
            user.setDefaultPassword(false);
            usersRepository.save(user);
        } else {
            logger.info("Default password does not match for username: {}", user.getUsername());
            throw new UserException(ErrorConstant.INVALID_DEFAULT_PASSWORD, HttpStatus.BAD_REQUEST);
        }
        logger.info("User password changed successfully for username: {}", user.getUsername());
    }


    public void deleteUserById(String userId) {
        logger.info("Attempting to delete user with ID: {}", userId);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = (CustomUserDetails) authentication.getPrincipal();
        String loggedInUserId = principal.getUserId();

        if (loggedInUserId.equals(userId)) {
            throw new UserException(
                    ErrorConstant.CANNOT_DELETE_OWN_ACCOUNT,
                    HttpStatus.BAD_REQUEST
            );
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorConstant.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));
        user.setDefunct(true);
        usersRepository.save(user);
        logger.info("Successfully deleted user with ID: {}", userId);
    }


    public void updateUserInfo(UserUpdateReqDTO userUpdateReqDTO) {
        if (userUpdateReqDTO == null || userUpdateReqDTO.getId() == null) {
            logger.info("Invalid request data for user update");
            throw new UserException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Users user = usersRepository.findById(userUpdateReqDTO.getId())
                .orElseThrow(() -> new UserException(ErrorConstant.USER_NOT_FOUND, HttpStatus.NOT_FOUND));


        usersRepository.findByUsernameIgnoreCase(userUpdateReqDTO.getUsername())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new UserException(ErrorConstant.USER_WITH_USERNAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                });

        usersRepository.findByEmailIgnoreCase(userUpdateReqDTO.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new UserException(ErrorConstant.USER_WITH_EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                });

        usersRepository.findByMobileNo(userUpdateReqDTO.getMobileNo())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    logger.info("User with mobile: {} already exists", userUpdateReqDTO.getMobileNo());
                    throw new UserException(ErrorConstant.USER_WITH_MOBILE_NO_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                });

        List<Roles> rolesList = rolesRepository.findAllByIDAndDefunct(userUpdateReqDTO.getRoleId(), false)
                .orElseThrow(() -> new UserException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST));

        List<Organization> organizationList = organizationMasterRepository.findAllByIDAndDefunct(userUpdateReqDTO.getOrganizationId(), false)
                .orElseThrow(() -> new UserException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (userUpdateReqDTO.getName() != null) user.setName(userUpdateReqDTO.getName());
        if (userUpdateReqDTO.getEmail() != null) user.setEmail(userUpdateReqDTO.getEmail());
        if (userUpdateReqDTO.getMobileNo() != null) user.setMobileNo(userUpdateReqDTO.getMobileNo());

        user.setRoles(rolesList);
        user.setOrganizations(organizationList);

        usersRepository.save(user);
        logger.info("User info updated for userId: {}", userUpdateReqDTO.getId());
    }


    @Async(value = "taskExecutor")
    private void getUserCredOnEmail(Users users, String generatePassword) {
        if (users == null || users.getEmail() == null) {
            logger.info("Invalid user data for email retrieval");
            throw new UserException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Context context = new Context();
        context.setVariable("name", users.getName());
        context.setVariable("username", users.getUsername());
        context.setVariable("password", generatePassword);
        context.setVariable("loginLink", "http://93.127.186.33:5173/login");
        String emailContent = templateEngine.process("RegUserEmail", context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
            message.setFrom(appEmail);
            message.setTo(users.getEmail());
            message.setSubject("Your Account Credentials");
            message.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
            logger.info("User credentials sent to email: {}", users.getEmail());
        } catch (Exception e) {
            throw new UserException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
