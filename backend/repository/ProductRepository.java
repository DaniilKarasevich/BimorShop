package com.bimor.BimorShop.repository;

import com.bimor.BimorShop.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByPrice(Integer price);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price = :price")
    List<Product> findByCategoryAndPrice(@Param("category") String category,
                                         @Param("price") Integer price);
}