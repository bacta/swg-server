package io.bacta.login.server.rest.controller;

import io.bacta.login.server.data.BactaAccount;
import io.bacta.login.server.repository.BactaAccountRepository;
import io.bacta.login.server.rest.model.CreateAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/accounts")
public final class AccountsController {
    //Can we make this AccountService<?> or something?
    private final BactaAccountRepository accountRepository;

    @Inject
    public AccountsController(BactaAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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

        BactaAccount account = new BactaAccount(request.getUsername(), request.getPassword());
        account = accountRepository.save(account);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.getId())
                .toUri();

        return ResponseEntity.created(location).body(account);
    }
}
