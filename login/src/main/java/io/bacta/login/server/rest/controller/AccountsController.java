package io.bacta.login.server.rest.controller;

import io.bacta.login.server.data.BactaAccount;
import io.bacta.login.server.repository.BactaAccountRepository;
import io.bacta.login.server.rest.model.CreateAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/accounts")
public final class AccountsController {
    private final BactaAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public AccountsController(BactaAccountRepository accountRepository,
                              PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<?> accounts() {
        return ResponseEntity.ok(accountRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> account(@PathVariable int id) {
        final BactaAccount account = accountRepository.findOne(id);

        if (account == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(account);
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

        BactaAccount account = new BactaAccount(request.getUsername(), encodedPassword);
        account = accountRepository.save(account);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.getId())
                .toUri();

        return ResponseEntity.created(location).body(account);
    }
}
