import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.intergamma.storea.storea.product.Product
import nl.intergamma.storea.storea.product.ProductService
import nl.intergamma.storea.storea.reservation.ProductReservation
import nl.intergamma.storea.storea.reservation.ReservationException
import nl.intergamma.storea.storea.reservation.ReservationRepository
import nl.intergamma.storea.storea.reservation.ReservationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class ReservationServiceTest {

  @MockK
  private lateinit var reservationRepository: ReservationRepository

  @MockK
  private lateinit var productService: ProductService

  @InjectMockKs
  private lateinit var reservationService: ReservationService

  @Test
  fun `test reserveStock with sufficient stock`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 5
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    val productCapturingSlot = slot<Product>()
    every { productService.findByProductCode(productCode) } returns(product)
    every { productService.updateArticle(capture(productCapturingSlot)) } answers  {productCapturingSlot.captured}

    // Act
    reservationService.reserveStock(productCode, quantity)

    // Assert
    assertEquals(productCapturingSlot.captured.stock, 10) // Stock reduced by reserved quantity
    assertEquals(productCapturingSlot.captured.reservedStock, 5) // Reserved stock increased by reserved quantity
  }

  @Test
  fun `test reserveStock with insufficient stock`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 10
    val product = Product(productCode = productCode, stock = 5, reservedStock = 0)
    every { productService.findByProductCode(productCode) } returns(product)

    // Act and Assert
    try {
      reservationService.reserveStock(productCode, quantity)
    } catch (e: ReservationException) {
      assertEquals(e.message, "Not enough stock available for reservation")
    }
  }

  @Test
  fun `test confirmReservation with valid reservation`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 5
    val product = Product(productCode = productCode, stock = 10, reservedStock = 5)
    val articleReservationId: Long = 1
    val reservation = ProductReservation(1,product,quantity, LocalDateTime.now().plusMinutes(5))
    val productCapturingSlot = slot<Product>()
    every { productService.updateArticle(capture( productCapturingSlot)) } answers {productCapturingSlot.captured}
    every { reservationRepository.findByIdOrNull(articleReservationId) } returns(reservation)
    every { reservationRepository.delete(any()) } just runs
    every { productService.findByProductCode(productCode) } returns(product)


    // Act
    val confirmedArticle = reservationService.confirmReservation(articleReservationId)

    // Assert
    assertEquals(productCapturingSlot.captured.stock, 5) // Stock reduced by confirmed quantity
    assertEquals(productCapturingSlot.captured.reservedStock, 0) // Reserved stock reduced to zero
    assertEquals(confirmedArticle, productCapturingSlot.captured)
  }

  @Test
  fun `test confirmReservation with insufficient reserved stock`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 10
    val product = Product(productCode = productCode, stock = 10, reservedStock = 5)
    val articleReservationId: Long = 1
    val reservation = ProductReservation(1,product,quantity, LocalDateTime.now().plusMinutes(5))
    every { reservationRepository.findByIdOrNull(articleReservationId) } returns(reservation)
    every { productService.findByProductCode(productCode) } returns(product)

    // Act and Assert
    try {
      reservationService.confirmReservation(articleReservationId)
    } catch (e: ReservationException) {
      assertEquals(e.message, "Invalid confirmation, insufficient reserved stock")
    }
  }

  @Test
  fun `test processExpiredReservation`() {
    // Arrange
    val productCode1 = "ABC123"
    val productCode2 = "XYZ789"
    val product1 = Product(productCode = productCode1, stock = 10, reservedStock = 5)
    val product2 = Product(productCode = productCode2, stock = 20, reservedStock = 8)
    val expiredReservation1 = ProductReservation(product = product1, quantity = 3, expiresAt = LocalDateTime.now().minusMinutes(10))
    val expiredReservation2 = ProductReservation(product = product2, quantity = 5, expiresAt = LocalDateTime.now().minusMinutes(5))

    val expiredReservations = listOf(expiredReservation1, expiredReservation2)


    val productCapturingSlot = mutableListOf<Product>()
    every { productService.updateArticle(capture( productCapturingSlot)) } returns product1
    every { reservationRepository.findExpiredReservations(any()) } returns expiredReservations
    every { productService.findByProductCode(productCode1) } returns product1
    every { productService.findByProductCode(productCode2) } returns product2

    // Act
    reservationService.processExpiredReservation()

    // Assert
    verify(exactly = 1) { reservationRepository.findExpiredReservations(any()) }
    verify(exactly = 1) { productService.findByProductCode(productCode1) }
    verify(exactly = 1) { productService.findByProductCode(productCode2) }
    verify(exactly = 1) { productService.updateArticle(product1) }
    verify(exactly = 1) { productService.updateArticle(product2) }

    // Check if the reserved stock and reservation list have been updated correctly
    assertEquals(product1.reservedStock, productCapturingSlot[0].reservedStock) // Reserved stock reduced by 3
    assertEquals(product2.reservedStock, productCapturingSlot[1].reservedStock) // Reserved stock reduced by 5
    assertEquals(product1.reservations.size, productCapturingSlot[0].reservations.size) // Expired reservation removed from the list
    assertEquals(product2.reservations.size, productCapturingSlot[1].reservations.size) // Expired reservation removed from the list
  }
}