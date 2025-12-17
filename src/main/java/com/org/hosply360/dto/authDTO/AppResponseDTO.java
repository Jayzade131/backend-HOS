package com.org.hosply360.dto.authDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.hosply360.dto.globalMasterDTO.PaginationDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter

public class AppResponseDTO {

    private String message;
    private Object data;
    private String status;
    private int statusCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PaginationDTO pagination;

    public static AppResponseDTO ok() {
        return ok(null);
    }

    public static AppResponseDTO ok(Object data) {
        AppResponseDTO dto = new AppResponseDTO();
        dto.setMessage("success");
        dto.setData(data);
        dto.setStatus(HttpStatus.OK.getReasonPhrase());
        dto.setStatusCode(HttpStatus.OK.value());
        return dto;
    }

    public static AppResponseDTO getOk(Object data, long total, long totalPages, long pageNumber) {
        AppResponseDTO dto = new AppResponseDTO();
        dto.setMessage("success");
        dto.setData(data);
        dto.setStatus(HttpStatus.OK.getReasonPhrase());
        dto.setStatusCode(HttpStatus.OK.value());

        PaginationDTO pagination = new PaginationDTO();
        pagination.setTotal(total);
        pagination.setTotalPages(totalPages);
        pagination.setPageNumber(pageNumber);
        dto.setPagination(pagination);

        return dto;
    }

    public static AppResponseDTO getOk2(Object data) {
        AppResponseDTO dto = new AppResponseDTO();
        dto.setMessage("success");
        dto.setData(data);
        dto.setStatus(HttpStatus.OK.getReasonPhrase());
        dto.setStatusCode(HttpStatus.OK.value());
        dto.setPagination(null); // no pagination info
        return dto;
    }


    public static AppResponseDTO badRequest(String message) {
        AppResponseDTO dto = new AppResponseDTO();
        dto.setMessage(message);
        dto.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        dto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return dto;
    }

    public static AppResponseDTO internalServerError(String message) {
        AppResponseDTO dto = new AppResponseDTO();
        dto.setMessage(message);
        dto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        dto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return dto;
    }

    public static AppResponseDTO customStatus(String message, HttpStatus status) {
        AppResponseDTO dto = new AppResponseDTO();
        dto.setMessage(message);
        dto.setStatus(status.getReasonPhrase());
        dto.setStatusCode(status.value());
        return dto;
    }
}