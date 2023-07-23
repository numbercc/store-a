package nl.intergamma.storea.storea.reservation

import jakarta.persistence.*
import nl.intergamma.storea.storea.product.Product
import java.time.LocalDateTime

@Entity
data class ProductReservation(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @ManyToOne
        @JoinColumn(name = "productCode")
        val product: Product,
        val quantity: Int,
        val expiresAt: LocalDateTime
)
