package com.bimor.BimorShop.controller;

import com.bimor.BimorShop.exception.ResourceNotFoundException;
import com.bimor.BimorShop.model.Product;
import com.bimor.BimorShop.service.ProductService;
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
@RequestMapping("/api/products")
@AllArgsConstructor
@Tag(name = "Product Controller", description = "API для управления продуктами")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Получить продукты",
            description = "Возвращает продукты с фильтрацией по категории и/или цене")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Продукты не найдены")
    public ResponseEntity<List<Product>> getProducts(
            @Parameter(
                    description = "Категория продукта",
                    example = "electronics")
            @RequestParam(value = "category", required = false) String category,
            @Parameter(
                    description = "Цена продукта",
                    example = "100")
            @RequestParam(value = "price", required = false) Integer price) {
        List<Product> products = productService.getProducts(category, price);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Не найдено продуктов по заданным критериям");
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить продукт по ID",
            description = "Возвращает продукт по указанному ID")
    @ApiResponse(
            responseCode = "200",
            description = "Продукт найден",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Продукт не найден")
    public ResponseEntity<Product> getProductById(
            @Parameter(
                    description = "ID продукта",
                    example = "1",
                    required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @Operation(
            summary = "Создать продукт",
            description = "Создает новый продукт")
    @ApiResponse(
            responseCode = "201",
            description = "Продукт создан",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные")
    public ResponseEntity<Product> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные продукта",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class)))
            @Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.saveProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить продукт",
            description = "Обновляет существующий продукт")
    @ApiResponse(
            responseCode = "200",
            description = "Продукт обновлен",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные")
    @ApiResponse(
            responseCode = "404",
            description = "Продукт не найден")
    public ResponseEntity<Product> updateProduct(
            @Parameter(
                    description = "ID продукта",
                    example = "1",
                    required = true)
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        product.setId(id);
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить продукт",
            description = "Удаляет продукт по ID")
    @ApiResponse(
            responseCode = "204",
            description = "Продукт удален")
    @ApiResponse(
            responseCode = "404",
            description = "Продукт не найден")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(
                    description = "ID продукта",
                    example = "1",
                    required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-several-products")
    @Operation(
            summary = "Создать несколько продуктов",
            description = "Создает список новых продуктов"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Продукты успешно созданы",
            content = @Content(schema = @Schema(implementation = Product.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные"
    )
    public ResponseEntity<List<Product>> createProductsBulk(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список продуктов для создания",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class)))
            @Valid @RequestBody List<Product> products) {

        List<Product> savedProducts = products.stream()
                .map(productService::saveProduct)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
    }
}