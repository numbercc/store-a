package nl.intergamma.storea.storea.reservation

import nl.intergamma.storea.storea.product.Product
import nl.intergamma.storea.storea.product.ProductRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
class ReservationIntegrationTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var productRepository: ProductRepository

  @Autowired
  private lateinit var reservationRepository: ReservationRepository

  @Test
  fun `test reserveStock endpoint`() {
    val productCode = "PRODUCT123"
    val quantity = 10
    val stock = 15
    productRepository.save(Product(productCode = productCode, stock = stock))
    mockMvc.perform(post("/reservations/$productCode/reserve")
      .param("quantity", quantity.toString())
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)
    val product = productRepository.findByProductCode(productCode)
    Assertions.assertEquals(product?.reservations?.size,1 )
    Assertions.assertEquals(product?.reservedStock,quantity )
  }

  @Test
  fun `test confirmReservation endpoint`() {
    val productCode = "PRODUCT123"
    val quantity = 10
    val stock = 15
    val product = productRepository.save(Product(productCode = productCode, stock = stock, reservedStock = quantity))

    val productReservation = reservationRepository.save(
      ProductReservation(
        product = product,
        quantity = quantity,
        expiresAt = LocalDateTime.now().plusMinutes(5)
      )
    )
    mockMvc.perform(post("/reservations/${productReservation.id}/confirm")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)
    val result = productRepository.findById(product.id).get()
    Assertions.assertEquals(result.reservations.size,0)
    Assertions.assertEquals(result.stock,5)
  }

//TODO make unhappy flows

//TODO make scheduled expired reservation tests
}