package nl.intergamma.storea.storea.product

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService (private val productRepository: ProductRepository) {
  fun addStock(productCode: String, quantity: Int): Product {
    val article = findByProductCode(productCode)
    article.addStock(quantity)
    return productRepository.save(article)
  }

  // Method to remove stock from an article
  fun decreaseStock(productCode: String, quantity: Int): Product {
    val article = findByProductCode(productCode)
    article.decreaseStock(quantity)
    return productRepository.save(article)
  }

  // Function to create a new article
  fun createArticle(productCode: String, stock: Int): Product {
    if (productRepository.existsByProductCode(productCode)) {
      throw IllegalArgumentException("Article with the same productCode already exists")
    }

    val product = Product(productCode = productCode, stock = stock)
    return productRepository.save(product)
  }

  // Function to delete an article
  fun deleteArticle(productCode: String) {
    val article = findByProductCode(productCode)

    productRepository.delete(article)
  }

  fun updateArticle(product: Product): Product{
    productRepository.findByIdOrNull(product.id)
        ?: throw ProductNotFoundException("Article not found")

    return productRepository.save(product)
  }

  fun findByProductCode(productCode: String): Product {
    return productRepository.findByProductCode(productCode)
        ?: throw ProductNotFoundException("Article not found")
  }


}
