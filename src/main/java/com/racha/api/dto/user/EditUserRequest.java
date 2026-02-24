package com.racha.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditUserRequest {
    private String name;
    private String email;
    private MultipartFile thumbnail;
    private String pixKey;
}
