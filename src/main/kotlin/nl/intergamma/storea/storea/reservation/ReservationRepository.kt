package nl.intergamma.storea.storea.reservation

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ReservationRepository: CrudRepository<ProductReservation,Long> {
  // Custom query to fetch expired reservations
  @Query("SELECT r FROM ProductReservation  r WHERE r.expiresAt <= :currentDateTime")
  fun findExpiredReservations(currentDateTime: LocalDateTime): List<ProductReservation>

}
