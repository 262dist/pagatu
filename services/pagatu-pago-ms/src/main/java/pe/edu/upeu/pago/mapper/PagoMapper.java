package pe.edu.upeu.pago.mapper;

import org.mapstruct.Mapper;
import pe.edu.upeu.pago.dto.PagoResponse;
import pe.edu.upeu.pago.entity.Pago;

@Mapper(componentModel = "spring")
public interface PagoMapper {
    PagoResponse toResponse(Pago pago);
}
