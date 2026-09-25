package pe.edu.upeu.auth.service;

import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
