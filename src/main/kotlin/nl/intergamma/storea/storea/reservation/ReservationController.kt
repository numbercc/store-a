package nl.intergamma.storea.storea.reservation

import nl.intergamma.storea.storea.product.Product
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reservations")
class ReservationController (private val reservationService: ReservationService){

  @PostMapping("/{productCode}/reserve")
  fun reserveStock(@PathVariable productCode: String, @RequestParam quantity: Int): ResponseEntity<ProductReservation> {
    return ResponseEntity.ok(reservationService.reserveStock(productCode, quantity))
  }

  @PostMapping("/{articleReservationId}/confirm")
  fun confirmReservation(@PathVariable articleReservationId: Long): ResponseEntity<Product> {
    return ResponseEntity.ok(reservationService.confirmReservation(articleReservationId))
  }
}