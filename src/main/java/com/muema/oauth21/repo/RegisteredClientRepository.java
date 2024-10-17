package com.muema.oauth21.repo;

import com.muema.oauth21.model.OAuth2RegisteredClientDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegisteredClientRepository extends CrudRepository<OAuth2RegisteredClientDTO, UUID> {
    Optional<OAuth2RegisteredClientDTO> findByClientId(String clientId);
}
