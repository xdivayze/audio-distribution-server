package com.zazabeyligisf.phonkdistro.title;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phonks")
@Builder
@Getter
public class Title {

    @Id
    private UUID id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "owner_name", length = 50, nullable = false, unique = true)
    private String owner;

    @Column(name = "path", nullable = false, unique = true)
    private String path = "src/main/songs/";

    @ElementCollection
    private Set<String> additions;

}
