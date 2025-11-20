package com.bimor.BimorShop.service;

import com.bimor.BimorShop.model.Account;
import com.bimor.BimorShop.model.Order;
import com.bimor.BimorShop.model.Product;
import com.bimor.BimorShop.repository.AccountRepository;
import com.bimor.BimorShop.repository.OrderRepository;
import com.bimor.BimorShop.repository.ProductRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"));
    }

    public List<Order> getOrdersByAccountId(Long accountId) {
        return orderRepository.findByAccountId(accountId);
    }

    public Order createOrder(Order order) {
        if (order.getAccount() == null || order.getAccount().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Account ID is required");
        }

        Account account = accountRepository.findById(order.getAccount().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found with id: "
                        + order.getAccount().getId()));
        order.setAccount(account);

        List<Product> products = productRepository.findAllById(order.getProductIds());
        if (products.size() != order.getProductIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "One or more products not found");
        }
        order.setProducts(products);

        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        List<Product> products = productRepository.findAllById(order.getProductIds());
        if (products.size() != order.getProductIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "One or more products not found");
        }
        order.setId(id);
        order.setProducts(products);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByProductCategoryJpql(String category) {
        List<Order> orders = orderRepository.findOrdersByProductCategoryJpql(category);
        if (orders.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Orders with category '" + category + "' not found");
        }
        return orders;
    }

    public List<Order> getOrdersByProductPriceNative(Integer price) {
        List<Order> orders = orderRepository.findOrdersByProductPriceNative(price);
        if (orders.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Orders with product price " + price + " not found");
        }
        return orders;
    }

    public Order addFavorite(Long accountId, Long productId) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        Order favorite = new Order();
        favorite.setAccount(acc);
        favorite.setFavorite(true);
        favorite.setOrderDate(java.time.LocalDateTime.now());
        favorite.setTotalPrice(0); // Это не заказ
        favorite.setProducts(List.of(product));

        return orderRepository.save(favorite);
    }

    public List<Product> getFavoritesByAccount(Long accountId) {
        List<Order> orders = orderRepository.findByAccountIdAndFavoriteTrue(accountId);

        return orders.stream()
                .flatMap(o -> o.getProducts().stream())
                .toList();
    }

    public void removeFavorite(Long accountId, Long productId) {
        List<Order> orders = orderRepository.findByAccountIdAndFavoriteTrue(accountId);

        orders.stream()
                .filter(o -> o.getProducts().stream().anyMatch(p -> p.getId().equals(productId)))
                .findFirst()
                .ifPresent(orderRepository::delete);
    }

}