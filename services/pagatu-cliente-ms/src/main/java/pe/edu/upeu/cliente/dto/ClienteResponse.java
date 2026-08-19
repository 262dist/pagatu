package pe.edu.upeu.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Long id;
    private String tipoPersona;
    private String dni;
    private String ruc;
    private String nombreCompleto;
    private String razonSocial;
    private String direccion;
    private String email;
    private String whatsapp;
}
