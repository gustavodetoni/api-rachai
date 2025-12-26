package com.racha.api.dto.user;

public record EditUserRequest (
     String name,
     String email,
     String thumbnail,
     String pixKey
) {}
