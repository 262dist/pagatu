package pe.edu.upeu.orden.repository;

import pe.edu.upeu.orden.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @Query("SELECT DISTINCT o FROM Orden o LEFT JOIN FETCH o.detalles")
    List<Orden> findAllConDetalles();
}
