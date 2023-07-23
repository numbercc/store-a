package nl.intergamma.storea.storea.product

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProductServiceTest {

  @MockK
  private lateinit var productRepository: ProductRepository

  @InjectMockKs
  private lateinit var productService: ProductService

  @Test
  fun `test addStock() with positive quantity`() {
    // Arrange
    val productCode = "ABC123"
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    every { productRepository.findByProductCode(productCode) }.returns(product)
    every { productRepository.save(product) }.returns(product)
    // Act
    val result = productService.addStock(productCode, 5)

    // Assert
    assertAll(
      { verify(exactly = 1) { productRepository.save(product) } },
      { assertThat(result.stock).isEqualTo(15) }
    )
  }

  @Test
  fun `test addStock() with negative quantity`() {
    // Arrange
    val productCode = "ABC123"
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    every { productRepository.findByProductCode(productCode) }.returns(product)
    every { productRepository.save(product) }.returns(product)

    // Act
    val result = productService.addStock(productCode, -5)

    // Assert
    verify(exactly = 1) { productRepository.save(product) }
    assertThat(result.stock).isEqualTo(5)

  }

  @Test
  fun `test removeStock() with sufficient stock`() {
    // Arrange
    val productCode = "ABC123"
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    val quantity = 5
    every { productRepository.findByProductCode(productCode) } returns (product)
    product.decreaseStock(quantity)
    every { productRepository.save(product) } returns (product)

    // Act
    val result = productService.decreaseStock(productCode, quantity)

    // Assert
    verify(exactly = 1) { productRepository.save(product) }
    assertThat(product.stock).isEqualTo(0)
  }

  @Test
  fun `test removeStock() with insufficient stock`() {
    // Arrange
    val productCode = "ABC123"
    val product = Product(productCode = productCode, stock = 10, reservedStock = 0)
    every { productRepository.findByProductCode(productCode) }.returns(product)

    // Act and Assert
    try {
      productService.decreaseStock(productCode, 15)
    } catch (e: IllegalArgumentException) {
      assertEquals(e.message, "Not enough stock to remove")
    }
  }

  @Test
  fun `test createArticle()`() {
    // Arrange
    val productCode = "ABC123"
    val stock = 10
    val newProduct = Product(productCode = productCode, stock = stock, reservedStock = 0)
    every { productRepository.existsByProductCode(productCode) } returns false
    every { productRepository.save(any()) } returns newProduct

    // Act
    val createdArticle = productService.createArticle(productCode, stock)

    // Assert
    verify(exactly = 1) { productRepository.save(any()) }
    assertThat(createdArticle.productCode).isEqualTo(productCode)
    assertThat(createdArticle.stock).isEqualTo(stock)
    assertThat(createdArticle.reservedStock).isEqualTo(0)

  }

  @Test
  fun `test createArticle() with existing article`() {
    // Arrange
    val productCode = "ABC123"
    val stock = 10
    every { productRepository.existsByProductCode(productCode) } returns true

    // Act and Assert
    try {
      productService.createArticle(productCode, stock)
    } catch (e: IllegalArgumentException) {
      assertEquals(e.message, "Article with the same productCode already exists")
    }
  }

  @Test
  fun `test deleteArticle()`() {
    // Arrange
    val productCode = "ABC123"
    val existingProduct = Product(productCode = productCode, stock = 10, reservedStock = 0)
    every { productRepository.findByProductCode(productCode) } returns (existingProduct)
    every { productRepository.delete(any()) } just runs
    // Act
    productService.deleteArticle(productCode)

    // Assert
    verify(exactly = 1) { productRepository.delete(any()) }

  }

  @Test
  fun `test deleteArticle() with non-existing article`() {
    // Arrange
    val productCode = "ABC123"
    every { productRepository.findByProductCode(productCode) } returns (null)

    // Act and Assert
    try {
      productService.deleteArticle(productCode)
    } catch (e: ProductNotFoundException) {
      assertEquals(e.message, "Article not found")
    }
  }
}