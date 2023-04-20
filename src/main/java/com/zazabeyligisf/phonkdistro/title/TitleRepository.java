package com.zazabeyligisf.phonkdistro.title;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TitleRepository extends JpaRepository<Title, UUID> {
    Optional<Title> findByName(String username);
}
