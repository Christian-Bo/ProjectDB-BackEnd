package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(RefreshRequest request);
    void logout(LogoutRequest request);
    UserDto me(String bearerToken);
}
