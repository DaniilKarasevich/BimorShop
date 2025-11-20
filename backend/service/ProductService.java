package com.bimor.BimorShop.service;

import com.bimor.BimorShop.model.Product;
import com.bimor.BimorShop.repository.OrderRepository;
import com.bimor.BimorShop.repository.ProductRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public List<Product> getProducts(String category, Integer price) {
        if (category != null && price != null) {
            return productRepository.findByCategoryAndPrice(category, price);
        } else if (category != null) {
            return productRepository.findByCategory(category);
        } else if (price != null) {
            return productRepository.findByPrice(price);
        }
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Удаляем продукт из всех заказов
        orderRepository.findByProductsContaining(product).forEach(order -> {
            order.getProducts().remove(product);
            orderRepository.save(order);
        });

        productRepository.delete(product);
    }
}
