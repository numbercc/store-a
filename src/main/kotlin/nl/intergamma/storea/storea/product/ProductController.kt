package nl.intergamma.storea.storea.product

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/products")
class ProductController (private val productService: ProductService) {

  @PostMapping("/{productCode}/add-stock")
  fun addStock(@PathVariable productCode: String, @RequestParam quantity: Int): ResponseEntity<Any> {
    val article = productService.addStock(productCode, quantity)
    return ResponseEntity.ok(article)
  }

  @PostMapping("/{productCode}/decrease-stock")
  fun decreaseStock(@PathVariable productCode: String, @RequestParam quantity: Int): ResponseEntity<Any> {
    val article = productService.decreaseStock(productCode, quantity)
    return ResponseEntity.ok(article)
  }

  @PostMapping("/")
  fun createArticle(@RequestParam productCode: String, @RequestParam stock: Int): ResponseEntity<Any> {
    val article = productService.createArticle(productCode, stock)
    return ResponseEntity.ok(article)
  }

  @GetMapping("/")
  fun getArticle(@RequestParam productCode:String ): ResponseEntity<Any> {
    val article = productService.findByProductCode(productCode)
    return ResponseEntity.ok(article)
  }

  @DeleteMapping("/{productCode}")
  fun deleteArticle(@PathVariable productCode: String): ResponseEntity<Any> {
    productService.deleteArticle(productCode)
    return ResponseEntity.ok("Article with productCode: $productCode deleted successfully")
  }

}
