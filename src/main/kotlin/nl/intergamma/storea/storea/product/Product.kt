package nl.intergamma.storea.storea.product

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import nl.intergamma.storea.storea.reservation.ProductReservation

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long=-1,
    @Column(unique=true)
    val productCode: String,
    var stock: Int = 0,
    var reservedStock: Int = 0,
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "product", fetch = FetchType.EAGER)
    @JsonIgnore
    var reservations: MutableList<ProductReservation> = mutableListOf()
) {
  // Method to add stock
  fun addStock(quantity: Int) {
    stock += quantity
  }

  // Method to remove stock
  fun decreaseStock(quantity: Int) {
    if (stock - quantity < 0) {
      throw IllegalArgumentException("Not enough stock to remove")
    }
    stock -= quantity
  }
}
