package com.zazabeyligisf.phonkdistro.title;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.collection.spi.PersistentSet;

import java.util.HashSet;

@AllArgsConstructor
@Getter
public class TitleDTO {
    private final String name;
    private final String owner;
    private final String additionals;
}
