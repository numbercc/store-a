package nl.intergamma.storea.storea.reservation

import nl.intergamma.storea.storea.product.Product
import nl.intergamma.storea.storea.product.ProductService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReservationService(private val productService: ProductService, private val reservationRepository: ReservationRepository) {
  @Scheduled(fixedDelay = 60000) // interval of checking if there is expired reservation
  fun processExpiredReservation() {

    val expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now())

    for (reservation in expiredReservations) {
      val product = productService.findByProductCode(reservation.product.productCode)
      product.reservedStock -= reservation.quantity
      product.reservations.remove(reservation)
      productService.updateArticle(product)
    }
  }

  fun reserveStock(productCode: String, quantity: Int): ProductReservation {
    val product = productService.findByProductCode(productCode)

    if (product.stock - product.reservedStock < quantity) {
      throw ReservationException("Not enough stock available for reservation")
    }

    product.reservedStock += quantity
    val expiresAt = LocalDateTime.now().plusMinutes(5)
    val reservation = ProductReservation(product = product, quantity = quantity, expiresAt = expiresAt)
    product.reservations.add(reservation)
    val updatedArticle = productService.updateArticle(product)
    return updatedArticle.reservations.first { it.expiresAt == expiresAt }
  }

  fun confirmReservation(articleReservationId: Long): Product {
    val reservation = reservationRepository.findByIdOrNull(articleReservationId)
        ?: throw ProductReservationNotFoundException("reservation not found")
    val product = productService.findByProductCode(reservation.product.productCode)
    if (product.reservedStock < reservation.quantity) {
      throw ReservationException("Invalid confirmation, insufficient reserved stock")
    }

    product.reservedStock -= reservation.quantity
    product.stock -= reservation.quantity
    product.reservations.remove(reservation)
    reservationRepository.delete(reservation)
    return productService.updateArticle(product)
  }
}