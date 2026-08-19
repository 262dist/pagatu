package pe.edu.upeu.orden.mapper;

import pe.edu.upeu.orden.dto.OrdenDetalleRequest;
import pe.edu.upeu.orden.dto.OrdenDetalleResponse;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.entity.OrdenDetalle;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrdenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Orden toEntity(OrdenRequest request);

    @Mapping(target = "items", source = "detalles")
    OrdenResponse toResponse(Orden orden);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orden", ignore = true)
    OrdenDetalle toEntity(OrdenDetalleRequest request);

    OrdenDetalleResponse toResponse(OrdenDetalle detalle);
}
