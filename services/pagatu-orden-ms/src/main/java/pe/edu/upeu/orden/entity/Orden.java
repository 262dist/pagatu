package pe.edu.upeu.orden.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "nombre_cliente", length = 150)
    private String nombreCliente;

    @Column(name = "direccion_cliente", length = 200)
    private String direccionCliente;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoOrden estado = EstadoOrden.CARRITO;

    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    @Builder.Default
    private String tipoComprobante = "BOLETA_SIMPLE";

    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago;

    @Column(name = "momento_pago", nullable = false, length = 20)
    @Builder.Default
    private String momentoPago = "ADELANTADO";

    private BigDecimal total;

    @Column(name = "expira_en")
    private LocalDateTime expiraEn;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrdenDetalle> detalles = new ArrayList<>();
}
