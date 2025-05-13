package com.project.mycars.dto;

import java.util.Date;

public record AuthResponse(
        String token,
        Integer userId,
        String firstName,
        String lastName,
        Date birthday,
        String phone,
        String login) {
}

