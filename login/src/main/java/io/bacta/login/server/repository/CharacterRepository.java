package io.bacta.login.server.repository;

import io.bacta.login.server.data.CharacterRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CharacterRepository extends CrudRepository<CharacterRecord, CharacterRecord.CharacterEntityKey> {
    List<CharacterRecord> findByBactaId(int bactaId);

    @Transactional
    void deleteByBactaId(int bactaId);
}