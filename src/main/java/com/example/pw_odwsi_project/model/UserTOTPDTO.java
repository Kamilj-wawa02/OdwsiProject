//package com.example.pw_odwsi_project.model;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.List;
//
//public record UserTOTPDTO(
//
//        @NotNull
//        @NotBlank
//        @Size(max = 255)
//        String username,
//
//        @NotNull
//        @NotBlank
//        @Size(max = 255)
//        String secretKey,
//
//        @NotNull
//        @NotBlank
//        int validationCode,
//
//        @NotNull
//        @NotBlank
//        List<Integer> scratchCodes
//
//) {
//}
