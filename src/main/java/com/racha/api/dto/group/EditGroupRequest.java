package com.racha.api.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditGroupRequest {

    private String name;

    private String description;

    private MultipartFile thumbnail;
}
