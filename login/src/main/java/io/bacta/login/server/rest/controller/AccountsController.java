package io.bacta.login.server.rest.controller;

import io.bacta.login.server.model.Account;
import io.bacta.login.server.repository.AccountRepository;
import io.bacta.login.server.rest.model.AccountListEntry;
import io.bacta.login.server.rest.model.CreateAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
public final class AccountsController {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Inject
    public AccountsController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public ResponseEntity<?> accounts() {
        final List<AccountListEntry> list = new ArrayList<>();

        for (final Account account : accountRepository.findAll()) {
            final AccountListEntry entry = new AccountListEntry(
                    account.getId(),
                    account.getUsername(),
                    account.getCreated());

            list.add(entry);
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> account(@PathVariable int id) {
        final Account account = accountRepository.findOne(id);

        if (account == null)
            return ResponseEntity.notFound().build();

        final AccountListEntry model = new AccountListEntry(
                account.getId(),
                account.getUsername(),
                account.getCreated());

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        accountRepository.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        LOGGER.info("Creating account {}", request.getUsername());

        final String encodedPassword = passwordEncoder.encode(request.getPassword());

        Account account = new Account(request.getUsername(), encodedPassword);
        account = accountRepository.save(account);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.getId())
                .toUri();

        return ResponseEntity.created(location).body(account);
    }
}
