package io.vxd.textadventure.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;

public class SaveGameRepository {
    private final ObjectMapper objectMapper;

    public SaveGameRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void save(Path path, SaveGame saveGame) throws IOException {
        objectMapper.writeValue(path.toFile(), saveGame);
    }

    public SaveGame load(Path path) throws IOException, IncompatibleSaveVersionException {
        SaveGame saveGame = objectMapper.readValue(path.toFile(), SaveGame.class);

        if (!SaveGame.FORMAT_VERSION.equals(saveGame.formatVersion())) {
            throw new IncompatibleSaveVersionException(SaveGame.FORMAT_VERSION, saveGame.formatVersion());
        }

        return saveGame;
    }
}