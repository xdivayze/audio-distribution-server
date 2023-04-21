package com.zazabeyligisf.phonkdistro.title;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class TitleService {
    public static final Path projectDir = Path.of(System.getProperty("user.dir")+"/");
    private static final File songDir;
    private static final int BUFFER_SIZE = 2048;
    final Gson gson;
    final TitleRepository repository;

    static {
        songDir = new File(projectDir.toFile(), "songs");
        songDir.mkdirs();
    }

    @Autowired
    public TitleService(Gson gson, TitleRepository repository) {
        this.gson = gson;
        this.repository = repository;
        log.info(projectDir.toString());
    }

    String playMusic(String n) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(repository.findByName(n).get().getPath()));
        return Base64.getEncoder().encodeToString(bytes);
    }

    String returnList(String n) {
        log.info("param " + n);
        Optional<Title> foundTitle = repository.findByName(n);
        if(foundTitle.isEmpty())
            throw  new RuntimeException("given parameter has no associated titles");
        return foundTitle.get().getName() + "," + foundTitle.get().getOwner();
    }

    ResponseEntity<String> handleUpload(String json) throws IOException, UnsupportedAudioFileException {
        JsonObject payload = JsonParser.parseString(json).getAsJsonObject();
        if (!payload.has("additions")) {
            payload.addProperty("additions", "");
        }
        UUID rn = UUID.randomUUID();
        repository.save(Title.builder()
                .id(rn)
                .name(payload.get("name").getAsString())
                .owner(payload.get("owner").getAsString())
                .additions(Set.of(payload.get("additions").getAsString().split(",")))
                .path(songDir.getAbsolutePath()+"/" + rn + "output.wav")
                .build());

        String fileB64 = payload.get("mp3file").getAsString();
        byte[] fileBytes = Base64.getDecoder().decode(fileB64);

        ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bais);

        byte[] buffer = new byte[BUFFER_SIZE];
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
