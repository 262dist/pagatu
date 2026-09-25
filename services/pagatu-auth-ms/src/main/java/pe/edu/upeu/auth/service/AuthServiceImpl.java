package pe.edu.upeu.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;
import pe.edu.upeu.auth.dto.RegistroRequest;
import pe.edu.upeu.auth.dto.RegistroResponse;
import pe.edu.upeu.auth.entity.Rol;
import pe.edu.upeu.auth.entity.Usuario;
import pe.edu.upeu.auth.exception.EmailYaRegistradoException;
import pe.edu.upeu.auth.repository.RolRepository;
import pe.edu.upeu.auth.repository.UsuarioRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generarToken(usuario);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpiracionSegundos())
                .build();
    }

    @Override
    @Transactional
    public RegistroResponse registrar(RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailYaRegistradoException(request.getEmail());
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE").orElseThrow();
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Set.of(rolCliente)))
                .build());

        return RegistroResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .build();
    }
}
