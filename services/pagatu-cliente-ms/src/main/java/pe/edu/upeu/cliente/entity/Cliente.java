package pe.edu.upeu.cliente.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_persona", nullable = false, length = 10)
    private String tipoPersona;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "ruc", length = 11)
    private String ruc;

    @Column(name = "nombre_completo", length = 150)
    private String nombreCompleto;

    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "whatsapp", length = 20)
    private String whatsapp;
}
