package com.org.hosply360.util.mapper;

import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;

public class HeaderFooterMapperUtil {

    public static PdfHeaderFooterDTO buildHeaderFooter(Organization organization) {

        Address addr = organization.getAddress();
        String formattedAddress = null;

        if (addr != null) {
            formattedAddress = String.join(", ",
                    addr.getBuildingFlat(),
                    addr.getStreet(),
                    addr.getCityName(),
                    addr.getStateName(),
                    addr.getCountryName()
            ) + " - " + addr.getPinCode();
        }

        return PdfHeaderFooterDTO.builder()
                .logo(organization.getOrgLogo() != null ? organization.getOrgLogo().getDocFile() : null)
                .hospitalName(organization.getOrganizationName())
                .tagline(organization.getOrganizationQuote())
                .address(formattedAddress)
                .contactNo(organization.getPhoneNumber())
                .gst(organization.getGstNo())
                .website(organization.getWebsite())
                .email(organization.getEmail())
                .build();
    }
}
