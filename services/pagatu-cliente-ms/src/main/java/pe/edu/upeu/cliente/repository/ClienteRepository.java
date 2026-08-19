package pe.edu.upeu.cliente.repository;

import pe.edu.upeu.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
