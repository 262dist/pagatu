package pe.edu.upeu.orden.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRequest {

    private Long idCliente;

    @NotBlank
    private String metodoPago;

    @NotEmpty
    @Valid
    private List<DetalleOrdenRequest> detalles;
}
