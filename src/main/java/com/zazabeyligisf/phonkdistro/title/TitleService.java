package com.zazabeyligisf.phonkdistro.title;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.collection.spi.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

//TODO SOMEHOW IMPROVE WAVE QUALITY OR ADD TO CHANGE MP3
//TODO SEND LISTS OF SONGS AND DISTRIBUTE THEM
@Slf4j
@Service
public class TitleService {
    final Gson gson;
    final TitleRepository repository;

    @Autowired
    public TitleService(Gson gson, TitleRepository repository) {
        this.gson = gson;
        this.repository = repository;
    }


    public ResponseEntity<String> handleUpload(String json) throws IOException, UnsupportedAudioFileException {


        JsonObject payload = JsonParser.parseString(json).getAsJsonObject();
        UUID rn = UUID.randomUUID();
        repository.save(Title.builder()
                .id(rn)
                .name(String.valueOf(payload.get("name")))
                .owner(String.valueOf(payload.get("owner")))
                .additions(Set.of(payload.get("additions").getAsString().split(",")))
                .path("src/main/songs/" + rn + "output.wav")
                .build());

        String fileB64 = payload.get("mp3file").getAsString();
        byte[] fileBytes = Base64.getDecoder().decode(fileB64);

        ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bais);

        int bytesPerFrame = ais.getFormat().getFrameSize();

        byte[] buffer = new byte[2048];
        int bytesRead = -1;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while ((bytesRead = ais.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        byte[] processed = outputStream.toByteArray();

        ByteArrayInputStream bais1 = new ByteArrayInputStream(processed);
        AudioInputStream ais1 = new AudioInputStream(bais1, new AudioFormat(44100, 16, 2, true, false), processed.length / 4);

        try {
            File outputWavFile = new File(Path.of(repository.findById(rn).get().getPath()).toUri());
            AudioSystem.write(ais1, AudioFileFormat.Type.WAVE, outputWavFile);
            return new ResponseEntity<>("Upload successful for:\n " + repository.findById(rn).get().getName(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Upload unsuccessful due to error:\n " + e.getMessage(), HttpStatus.valueOf(500));
        }



    }
}
