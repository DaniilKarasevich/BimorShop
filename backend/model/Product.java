package com.bimor.BimorShop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Название товара обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @Column(nullable = false)
    @Positive(message = "Цена должна быть положительным числом")
    private int price;

    @Column(nullable = false)
    @NotBlank(message = "Категория товара обязательна")
    @Size(max = 100, message = "Категория не должна превышать 100 символов")
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({"products", "orders"})
    private Account account;

    @ManyToMany(mappedBy = "products")
    @JsonIgnoreProperties({"account", "products"})
    private List<Order> orders;
}