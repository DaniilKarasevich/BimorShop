package com.bimor.BimorShop.repository;

import com.bimor.BimorShop.model.Order;
import com.bimor.BimorShop.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByAccountId(Long accountId);

    List<Order> findByProductsContaining(Product product);

    List<Order> findByAccountIdAndFavoriteTrue(Long accountId);

    @Query("SELECT o FROM Order o JOIN o.products p WHERE p.category = :category")
    List<Order> findOrdersByProductCategoryJpql(String category);

    @Query(
            value =
                    """
                    SELECT DISTINCT o.* FROM orders o
                    JOIN order_product op ON o.id = op.order_id
                    JOIN products p ON op.product_id = p.id
                    WHERE p.price = :price
                    """,
            nativeQuery = true)
    List<Order> findOrdersByProductPriceNative(Integer price);
}