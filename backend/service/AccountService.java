package com.bimor.BimorShop.service;

import com.bimor.BimorShop.model.Account;
import com.bimor.BimorShop.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> getAccountByNickname(String nickname) {
        return accountRepository.findByNickname(nickname);
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Каскадное удаление заказов
        if (account.getOrders() != null) {
            account.getOrders().clear();
        }

        accountRepository.delete(account); // Удаляем аккаунт
    }

    @Transactional
    public Account updateAccount(Long id, Account updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Копируем только нужные поля (игнорируем заказы)
        existingAccount.setNickname(updatedAccount.getNickname());
        existingAccount.setFirstName(updatedAccount.getFirstName());
        existingAccount.setLastName(updatedAccount.getLastName());
        existingAccount.setEmail(updatedAccount.getEmail());

        return accountRepository.save(existingAccount);
    }

    // Регистрация
    public Account register(Account account) {
        if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
            throw new RuntimeException("Email уже зарегистрирован");
        }
        if (accountRepository.findByPhone(account.getPhone()).isPresent()) {
            throw new RuntimeException("Телефон уже зарегистрирован");
        }
        return accountRepository.save(account);
    }

    // Вход
    public Account login(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email не найден"));

        if (!account.getPassword().equals(password)) {
            throw new RuntimeException("Неверный пароль");
        }

        return account;
    }
}