package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class AuthRequestDTO {
    String username;
    String password;
    @Nullable Boolean requestAdmin;
}
