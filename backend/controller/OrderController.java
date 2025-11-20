package com.bimor.BimorShop.controller;

import com.bimor.BimorShop.exception.ResourceNotFoundException;
import com.bimor.BimorShop.exception.ValidationException;
import com.bimor.BimorShop.model.Order;
import com.bimor.BimorShop.model.Product;
import com.bimor.BimorShop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@Tag(name = "Order Controller", description = "API для управления заказами")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает список всех заказов")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить заказ по ID",
            description = "Возвращает заказ по указанному ID")
    @ApiResponse(
            responseCode = "200",
            description = "Заказ найден",
            content = @Content(schema = @Schema(implementation = Order.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Заказ не найден")
    public ResponseEntity<Order> getOrderById(
            @Parameter(
                    description = "ID заказа",
                    example = "1",
                    required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    @Operation(
            summary = "Создать заказ",
            description = "Создает новый заказ")
    @ApiResponse(
            responseCode = "201",
            description = "Заказ создан",
            content = @Content(schema = @Schema(implementation = Order.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные")
    public ResponseEntity<Order> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные заказа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Order.class)))
            @Valid @RequestBody Order order) {
        if (order.getProductIds() == null || order.getProductIds().isEmpty()) {
            throw new ValidationException("Необходимо указать ID продуктов");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(order));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить заказ",
            description = "Обновляет существующий заказ")
    @ApiResponse(
            responseCode = "200",
            description = "Заказ обновлен",
            content = @Content(schema = @Schema(implementation = Order.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные")
    @ApiResponse(
            responseCode = "404",
            description = "Заказ не найден")
    public ResponseEntity<Order> updateOrder(
            @Parameter(
                    description = "ID заказа",
                    example = "1",
                    required = true)
            @PathVariable Long id,
            @Valid @RequestBody Order order) {
        if (order.getProductIds() == null || order.getProductIds().isEmpty()) {
            throw new ValidationException("Необходимо указать ID продуктов");
        }
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить заказ",
            description = "Удаляет заказ по ID")
    @ApiResponse(
            responseCode = "204",
            description = "Заказ удален")
    @ApiResponse(
            responseCode = "404",
            description = "Заказ не найден")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(
                    description = "ID заказа",
                    example = "1",
                    required = true)
            @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/favorite")
    @Operation(summary = "Добавить товар в избранное")
    public ResponseEntity<Order> addFavorite(
            @RequestParam Long accountId,
            @RequestParam Long productId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.addFavorite(accountId, productId));
    }

    @GetMapping("/favorite/{accountId}")
    @Operation(summary = "Получить избранные товары пользователя")
    public ResponseEntity<List<Product>> getFavorites(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(orderService.getFavoritesByAccount(accountId));
    }

    @DeleteMapping("/favorite")
    @Operation(summary = "Удалить товар из избранного")
    public ResponseEntity<Void> removeFavorite(
            @RequestParam Long accountId,
            @RequestParam Long productId) {

        orderService.removeFavorite(accountId, productId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/filter/by-category-jpql")
    @Operation(
            summary = "Фильтр заказов по категории (JPQL)",
            description = "Возвращает заказы, содержащие продукты указанной категории")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<List<Order>> getOrdersByProductCategoryJpql(
            @Parameter(
                    description = "Категория продукта",
                    example = "electronics",
                    required = true)
            @RequestParam String category) {
        return ResponseEntity.ok(orderService.getOrdersByProductCategoryJpql(category));
    }

    @GetMapping("/filter/by-price-native")
    @Operation(
            summary = "Фильтр заказов по цене (Native Query)",
            description = "Возвращает заказы, содержащие продукты с указанной ценой")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<List<Order>> getOrdersByProductPriceNative(
            @Parameter(
                    description = "Цена продукта",
                    example = "100",
                    required = true)
            @RequestParam Integer price) {
        return ResponseEntity.ok(orderService.getOrdersByProductPriceNative(price));
    }
}