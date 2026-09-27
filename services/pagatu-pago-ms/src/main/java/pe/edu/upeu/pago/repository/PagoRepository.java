package pe.edu.upeu.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pago.entity.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
