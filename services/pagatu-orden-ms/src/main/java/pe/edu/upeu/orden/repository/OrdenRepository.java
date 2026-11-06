package pe.edu.upeu.orden.repository;

import pe.edu.upeu.orden.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByIdCliente(String idCliente);
}
