package com.zazabeyligisf.phonkdistro.title;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@RestController
@RequestMapping("api/v1")
public class TitleController {
    final TitleRepository repository;
    final TitleService service;

    @Autowired
    public TitleController(TitleRepository repository, TitleService service) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping("/persist")
    public int persistTitle(@RequestBody String json) {
        return service.persistTitle(json);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(@RequestBody String json) throws UnsupportedAudioFileException, IOException {
        return service.handleUpload(json);
    }
}
