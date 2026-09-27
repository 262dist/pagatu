package pe.edu.upeu.orden.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCreadaEvento {

    private String tipoEvento;
    private Long ordenId;
    private Long idCliente;
    private BigDecimal total;
    private String metodoPago;
    private String origen;
    private Long timestamp;
}
