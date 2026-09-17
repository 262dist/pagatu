package pe.edu.upeu.orden.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenRequest {

    @NotNull
    private Long idProducto;

    @NotNull
    @Positive
    private Integer cantidad;
}
