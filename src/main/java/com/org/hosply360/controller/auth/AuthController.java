package com.org.hosply360.controller.auth;


import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.authDTO.AuthRequestDTO;
import com.org.hosply360.dto.authDTO.PasswordReqDTO;
import com.org.hosply360.dto.authDTO.UserRegisterReqDto;
import com.org.hosply360.dto.authDTO.UserUpdateReqDTO;
import com.org.hosply360.service.auth.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.AUTH_URL)
public class AuthController {

    private final AuthService authService;

    @PostMapping(EndpointConstants.LOGIN_USER)
    public ResponseEntity<AppResponseDTO> userLogin(@RequestBody AuthRequestDTO authRequestDTO) {

        return ResponseEntity.ok(AppResponseDTO.ok(authService.authenticate(authRequestDTO)));

    }

    @PostMapping(EndpointConstants.REG_USER)
    public ResponseEntity<AppResponseDTO> userRegister(@RequestBody UserRegisterReqDto userRegisterReqDto) {

        return ResponseEntity.ok(AppResponseDTO.ok(authService.registerUser(userRegisterReqDto)));
    }


    @GetMapping(EndpointConstants.CHECK_USERNAME_EXISTS)
    public ResponseEntity<AppResponseDTO> checkUsernameExists(@PathVariable String username) {
        boolean result = authService.checkUsernameExists(username);

        if (result) {
            return ResponseEntity.ok(AppResponseDTO.ok("Username already exists"));
        } else {
            return ResponseEntity.ok(AppResponseDTO.ok("Username is available"));
        }
    }

    @GetMapping(EndpointConstants.CHECK_EMAIL_EXISTS)
    public ResponseEntity<AppResponseDTO> checkEmailExists(@PathVariable String email) {
        boolean result = authService.checkEmail(email);

        if (result) {
            return ResponseEntity.ok(AppResponseDTO.ok("Email already exists"));
        } else {
            return ResponseEntity.ok(AppResponseDTO.ok("Email is available"));
        }
    }

    @GetMapping(EndpointConstants.CHECK_MOBILE_NO_EXISTS)
    public ResponseEntity<AppResponseDTO> checkMobileNoExists(@PathVariable String mobileNo) {
        boolean result = authService.checkMobileExists(mobileNo);

        if (result) {
            return ResponseEntity.ok(AppResponseDTO.ok("mobileNo already exists"));
        } else {
            return ResponseEntity.ok(AppResponseDTO.ok("mobileNo is available"));
        }
    }

    @GetMapping(EndpointConstants.USERS_BY_ORG_ID)
    public ResponseEntity<AppResponseDTO> getAllUsersByOrganizationAndDefunct(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(
                AppResponseDTO.ok(authService.getAllUsersByOrganizationAndDefunct(organizationId))
        );
    }

    @GetMapping(EndpointConstants.DOCTOR_USERS_BY_ORG_ID)
    public ResponseEntity<AppResponseDTO> getAllDocUsers(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(
                AppResponseDTO.ok(authService.getAllDoctorUsers(organizationId))
        );
    }

    @PostMapping(EndpointConstants.CHANGE_DEFAULT_PASSWORD)
    public ResponseEntity<AppResponseDTO> changeDefaultPassToNewPass(
            @RequestBody PasswordReqDTO passwordReqDTO) {

        authService.changeNewPasswordUsingDefaultPassword(passwordReqDTO);
        return ResponseEntity.ok(
                AppResponseDTO.ok("Password changed successfully")
        );
    }

    @DeleteMapping(EndpointConstants.DELETE_USER_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteUser(@PathVariable String userId) {
        authService.deleteUserById(userId);
        return ResponseEntity.ok(
                AppResponseDTO.ok("User deleted successfully")
        );
    }

    @PutMapping(EndpointConstants.REG_USER)
    public ResponseEntity<AppResponseDTO> updateUser(@RequestBody UserUpdateReqDTO userUpdateReqDTO) {

        authService.updateUserInfo(userUpdateReqDTO);
        return ResponseEntity.ok(AppResponseDTO.ok("User updated successfully"));
    }

}


