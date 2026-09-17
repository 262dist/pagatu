package pe.edu.upeu.orden.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponse {
    private Long id;
    private Long idCliente;
    private LocalDateTime fechaCreacion;
    private String estado;
    private BigDecimal total;
    private List<DetalleOrdenResponse> detalles;
}
