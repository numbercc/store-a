package nl.intergamma.storea.storea.product

import org.springframework.data.repository.CrudRepository

interface ProductRepository: CrudRepository<Product,Long> {
  fun findByProductCode(productCode: String): Product?
  fun existsByProductCode(productCode: String): Boolean

}
