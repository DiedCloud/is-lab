package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.UserType;

@Data
@AllArgsConstructor
public class UserDTO {
    UserType type;
    String login;
}
