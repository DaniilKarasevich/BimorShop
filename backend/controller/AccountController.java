package com.bimor.BimorShop.controller;

import com.bimor.BimorShop.exception.ResourceNotFoundException;
import com.bimor.BimorShop.model.Account;
import com.bimor.BimorShop.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Tag(name = "Account Controller", description = "API для управления аккаунтами")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(
            summary = "Получить все аккаунты",
            description = "Возвращает список всех аккаунтов")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Account.class)))
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить аккаунт по ID",
            description = "Возвращает аккаунт по указанному ID")
    @ApiResponse(
            responseCode = "200",
            description = "Аккаунт найден",
            content = @Content(schema = @Schema(implementation = Account.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Аккаунт не найден")
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<Account> getAccountById(
            @Parameter(
                    description = "ID аккаунта",
                    example = "1",
                    required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + id)));
    }

    @PostMapping
    @Operation(
            summary = "Создать аккаунт",
            description = "Создает новый аккаунт")
    @ApiResponse(
            responseCode = "201",
            description = "Аккаунт создан",
            content = @Content(schema = @Schema(implementation = Account.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные")
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<Account> createAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные аккаунта",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Account.class)))
            @Valid @RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.saveAccount(account));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить аккаунт",
            description = "Обновляет существующий аккаунт")
    @ApiResponse(
            responseCode = "200",
            description = "Аккаунт обновлен",
            content = @Content(schema = @Schema(implementation = Account.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные")
    @ApiResponse(
            responseCode = "404",
            description = "Аккаунт не найден")
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(id, account));
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить аккаунт",
            description = "Удаляет аккаунт по ID")
    @ApiResponse(
            responseCode = "204",
            description = "Аккаунт удален")
    @ApiResponse(
            responseCode = "404",
            description = "Аккаунт не найден")
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(
                    description = "ID аккаунта",
                    example = "1",
                    required = true)
            @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        Account saved = accountService.register(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody LoginRequest request) {
        Account account = accountService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(account);
    }
}