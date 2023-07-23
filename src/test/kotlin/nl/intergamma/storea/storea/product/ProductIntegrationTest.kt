package nl.intergamma.storea.storea.product

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var productRepository: ProductRepository

  @BeforeEach
  fun setUp() {
    // Optional: Add any setup logic here before each test

  }

  @AfterEach
  fun deleteAll(){
    productRepository.deleteAll()
  }

  @Test
  fun `test addStock endpoint`() {
    val productCode = "PRODUCT123"
    val quantity = 10
    val stock = 5
    val product = Product(productCode = productCode, stock = stock)
    productRepository.save(product)
    mockMvc.perform(post("/products/$productCode/add-stock")
      .param("quantity", quantity.toString())
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)

    val result = productRepository.findByProductCode(productCode)
    assertEquals(quantity+stock, result?.stock)
  }

  @Test
  fun `test decreaseStock endpoint`() {
    val productCode = "PRODUCT456"
    val initialStock = 20
    val quantityToDecrease = 5
    val expectedStockAfterDecrease = initialStock - quantityToDecrease
    val product = Product(productCode = productCode, stock = initialStock)
    productRepository.save(product)
    mockMvc.perform(post("/products/$productCode/decrease-stock")
      .param("quantity", quantityToDecrease.toString())
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)

    val result = productRepository.findByProductCode(productCode)
    assertEquals(expectedStockAfterDecrease, result?.stock)
  }

  @Test
  fun `test createArticle endpoint`() {
    val productCode = "NEW_PRODUCT"
    val stock = 100

    mockMvc.perform(post("/products/")
      .param("productCode", productCode)
      .param("stock", stock.toString())
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)

    val article = productRepository.findByProductCode(productCode)
    assertEquals(stock, article?.stock)
  }

  @Test
  fun `test deleteArticle endpoint`() {
    val productCode = "PRODUCT789"
    val product = Product(productCode = productCode)
    productRepository.save(product)
    mockMvc.perform(delete("/products/$productCode")
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)

    val result = productRepository.findByProductCode(productCode)
    assertEquals(null, result)
  }

  @Test
  fun `test getArticle endpoint`() {
    val productCode = "PRODUCT123"
    val product = productRepository.save(Product(productCode = productCode))

    val result = mockMvc.perform(get("/products/")
      .param("productCode", productCode)
      .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)
      .andReturn()

    val resultProduct: Product = jacksonObjectMapper().readValue(result.response.contentAsString)

    assertEquals(resultProduct, product)
  }
}