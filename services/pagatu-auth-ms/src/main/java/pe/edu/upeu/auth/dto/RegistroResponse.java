package pe.edu.upeu.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class RegistroResponse {

    private Long id;
    private String email;
}
