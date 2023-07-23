package nl.intergamma.storea.storea.product

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
class ProductControllerTest {
  @MockK
  private lateinit var productService: ProductService

  @InjectMockKs
  private lateinit var productController: ProductController

  @Test
  fun `test addStock with valid request`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 5
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    every { productService.addStock(productCode, quantity) } returns product

    // Act
    val response = productController.addStock(productCode, quantity)

    // Assert
    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(product, response.body)
  }

  @Test
  fun `test decreaseStock with valid request`() {
    // Arrange
    val productCode = "ABC123"
    val quantity = 5
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    every { productService.decreaseStock(productCode, quantity) } returns product

    // Act
    val response = productController.decreaseStock(productCode, quantity)

    // Assert
    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(product, response.body)
  }

  @Test
  fun `test createArticle with valid request`() {
    // Arrange
    val productCode = "ABC123"
    val stock = 10
    val product = Product(productCode = productCode, stock = stock, reservedStock = 0)
    every { productService.createArticle(productCode, stock) } returns product

    // Act
    val response = productController.createArticle(productCode, stock)

    // Assert
    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(product, response.body)
  }

  @Test
  fun `test deleteArticle with valid request`() {
    // Arrange
    val productCode = "ABC123"
    every { productService.deleteArticle(productCode) } just runs

    // Act
    val response = productController.deleteArticle(productCode)

    // Assert
    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals("Article with productCode: $productCode deleted successfully", response.body)
  }
}