package pe.edu.upeu.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    @NotBlank
    @Pattern(regexp = "NATURAL|JURIDICA")
    private String tipoPersona;

    @Pattern(regexp = "\\d{8}")
    private String dni;

    @Pattern(regexp = "\\d{11}")
    private String ruc;

    @Size(max = 150)
    private String nombreCompleto;

    @Size(max = 150)
    private String razonSocial;

    @Size(max = 255)
    private String direccion;

    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String whatsapp;
}
