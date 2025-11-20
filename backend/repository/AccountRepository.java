package com.bimor.BimorShop.repository;

import com.bimor.BimorShop.model.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNickname(String nickname);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByPhone(String phone);

}