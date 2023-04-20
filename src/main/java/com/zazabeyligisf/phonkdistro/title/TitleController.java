package com.zazabeyligisf.phonkdistro.title;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;
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

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(@RequestBody String json) throws UnsupportedAudioFileException, IOException {
        return service.handleUpload(json);
    }

    @GetMapping("/querify")
    public String handleQuery(@RequestParam String s) {
        return service.returnList(s);
    }

    @GetMapping("/play")
    public String playMusic(@RequestParam String s) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return service.playMusic(s);
    }
}
