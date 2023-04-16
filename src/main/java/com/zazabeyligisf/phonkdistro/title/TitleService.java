package com.zazabeyligisf.phonkdistro.title;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

//TODO INCREASE BUFFER SIZE
//TODO SOMEHOW IMPROVE WAVE QUALITY

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

    public int persistTitle(String json) {
        try {
            TitleDTO t = gson.fromJson(json, TitleDTO.class);
            UUID rD = UUID.randomUUID();
            repository.save(Title.builder()
                    .additions(Set.of(t.getAdditionals().split(",")))
                    .id(rD)
                    .path("src/main/songs/" + rD + ".mp3")
                    .owner(t.getOwner())
                    .name(t.getName())
                    .build());
            log.info("Successfully persisted title: \n" + json);
            return 0;
        } catch (Exception e) {
            throw e;
//            return -1;
        }
    }

    public ResponseEntity<String> handleUpload(String json) throws IOException, UnsupportedAudioFileException {



        JsonObject payload = JsonParser.parseString(json).getAsJsonObject();
        String fileB64 = payload.get("mp3file").getAsString();
        byte[] fileBytes = Base64.getDecoder().decode(fileB64);

        ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bais);

        int bytesPerFrame = ais.getFormat().getFrameSize();
        int bufferSize = 2048*bytesPerFrame;

        byte[] buffer = new byte[2048];
        int bytesRead = -1;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while ((bytesRead = ais.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        byte[] processed = outputStream.toByteArray();

        AudioFormat wavFormat = new AudioFormat(44100, 16, 1, true, false);

        ByteArrayInputStream bais1 = new ByteArrayInputStream(processed);
        AudioInputStream ais1 = new AudioInputStream(bais1, new AudioFormat(44100, 16, 2, true, false), processed.length / 4);

        File outputWavFile = new File("src/main/songs/"+payload.get("name")+"output.wav");
        AudioSystem.write(ais1, AudioFileFormat.Type.WAVE, outputWavFile);

        return null;
    }
}
