package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.Group;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerDTO {

    private String id;

    @NotBlank(message = "Ledger name cannot be blank")
    private String ledgerName;

    private AddressDTO address;

    private String organizationId;

    private Group group;

    @Size(max = 10, message = "Mobile number cannot exceed 10 characters")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^[2-9]{1}[0-9]{11}$",
            message = "Invalid Aadhaar number"
    )
    private String aadhaar;

    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Invalid PAN number"
    )
    private String pan;

    private String contractPerson;

    private String bankAccountName;

    private String branch;

    private String bankName;

    private String bankAccountNumber;

    @Pattern(
            regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
            message = "Invalid IFSC code"
    )
    private String ifscCode;

    private Boolean registeredWithGst;

    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{1}Z[A-Z0-9]{1}$",
            message = "Invalid GST number"
    )
    private String gstNumber;





}
