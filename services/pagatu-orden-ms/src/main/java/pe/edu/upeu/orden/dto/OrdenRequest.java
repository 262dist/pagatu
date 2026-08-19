package pe.edu.upeu.orden.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrdenRequest {

    @NotNull
    private Long idCliente;

    @Pattern(regexp = "BOLETA_SIMPLE|BOLETA_CON_DNI|FACTURA")
    private String tipoComprobante;

    @NotNull
    @Pattern(regexp = "TARJETA|YAPE_PLIN|TRANSFERENCIA|PAGO_EFECTIVO")
    private String metodoPago;

    @Pattern(regexp = "ADELANTADO|CONTRA_ENTREGA")
    private String momentoPago;

    @NotEmpty
    private List<@Valid OrdenDetalleRequest> items;
}
