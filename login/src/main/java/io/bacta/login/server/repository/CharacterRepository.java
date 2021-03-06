package io.bacta.login.server.repository;

import io.bacta.login.server.model.Character;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CharacterRepository extends CrudRepository<Character, Character.CharacterEntityKey> {
    List<Character> findByBactaId(int bactaId);

    @Transactional
    void deleteByBactaId(int bactaId);
}