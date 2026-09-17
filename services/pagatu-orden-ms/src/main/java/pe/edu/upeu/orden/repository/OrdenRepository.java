package pe.edu.upeu.orden.repository;

import pe.edu.upeu.orden.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
