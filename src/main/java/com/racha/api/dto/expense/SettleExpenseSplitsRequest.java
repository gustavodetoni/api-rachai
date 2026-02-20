package com.racha.api.dto.expense;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettleExpenseSplitsRequest {

    @NotEmpty(message = "Pelo menos um ID de expense split deve ser fornecido")
    private List<UUID> expenseSplitIds;

    private MultipartFile evidence;
}
