package nl.intergamma.storea.storea.reservation

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.intergamma.storea.storea.product.Product
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class ReservationControllerTest {

  @MockK
  private lateinit var reservationService: ReservationService

  @InjectMockKs
  private lateinit var reservationController: ReservationController

  @Test
  fun `test reserveStock with valid request`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 5
    val productReservation = ProductReservation(product = Product(productCode = productCode, stock = 0), expiresAt = LocalDateTime.now().plusMinutes(5), quantity = 5)
    every { reservationService.reserveStock(productCode, quantity) } returns productReservation

    // Act
    val response = reservationController.reserveStock(productCode, quantity)

    // Assert
    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(productReservation, response.body)
  }

  @Test
  fun `test reserveStock with insufficient stock`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 8
    every { reservationService.reserveStock(productCode, quantity) } throws ReservationException("Not enough stock available for reservation")

    // Act and Assert
    assertThrows<ReservationException> {
      reservationController.reserveStock(productCode, quantity)
    }
  }

  @Test
  fun `test confirmReservation with valid request`() {
    // Arrange
    val articleReservationId = 123L
    val product = Product(productCode = "ABC123", stock = 10, reservedStock = 5)
    every { reservationService.confirmReservation(articleReservationId) } returns product

    // Act
    val response = reservationController.confirmReservation(articleReservationId)

    // Assert
    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(product, response.body)
  }

  @Test
  fun `test confirmReservation with invalid reservation`() {
    // Arrange
    val articleReservationId = 123L
    every { reservationService.confirmReservation(articleReservationId) } throws ReservationException("Invalid reservation")

    // Act and Assert
    assertThrows<ReservationException> {
      reservationController.confirmReservation(articleReservationId)
    }
  }
}