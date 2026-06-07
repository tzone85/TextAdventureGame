package io.vxd.textadventure.ui;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.application.GameEngine;
import io.vxd.textadventure.domain.Room;
import io.vxd.textadventure.domain.WorldDefinition;
import io.vxd.textadventure.domain.WorldState;
import io.vxd.textadventure.infrastructure.dto.WorldStateDto;
import io.vxd.textadventure.infrastructure.persistence.IncompatibleSaveVersionException;
import io.vxd.textadventure.infrastructure.persistence.SaveGame;
import io.vxd.textadventure.infrastructure.persistence.SaveGameRepository;

import java.io.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class Repl {
    private final GameEngine gameEngine;
    private final SaveGameRepository saveGameRepository;
    private final AnsiPalette palette;
    private final Parser parser;

    private InputStream inputStream;
    private PrintStream outputStream;

    public Repl(GameEngine gameEngine, SaveGameRepository saveGameRepository, AnsiPalette palette, Parser parser) {
        this.gameEngine = gameEngine;
        this.saveGameRepository = saveGameRepository;
        this.palette = palette;
        this.parser = parser;
        this.inputStream = System.in;
        this.outputStream = System.out;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void run(String worldPath, String loadPath) throws IOException, IncompatibleSaveVersionException {
        WorldDefinition worldDefinition = WorldLoader.loadWorld(worldPath);

        WorldState worldState;
        if (loadPath != null) {
            SaveGame saveGame = saveGameRepository.load(Path.of(loadPath));
            worldState = saveGame.state().toDomain();
        } else {
            worldState = initializeNewGame(worldDefinition);
        }

        printInitialRoom(worldDefinition, worldState);
        runMainLoop(worldDefinition, worldState);
    }

    private WorldState initializeNewGame(WorldDefinition worldDefinition) {
        Map<String, Set<String>> itemLocations = new HashMap<>();

        // Initialize room item locations
        for (String roomId : worldDefinition.rooms().keySet()) {
            Room room = worldDefinition.rooms().get(roomId);
            Set<String> roomItems = new HashSet<>(room.itemIds());
            itemLocations.put(roomId, roomItems);
        }

        // Initialize inventory
        itemLocations.put("inventory", new HashSet<>(worldDefinition.startInventory()));

        return new WorldState(
            worldDefinition.startRoomId(),
            itemLocations,
            Map.of()
        );
    }

    private void printInitialRoom(WorldDefinition worldDefinition, WorldState worldState) {
        Room currentRoom = worldDefinition.rooms().get(worldState.currentRoomId());
        if (currentRoom != null) {
            outputStream.println(palette.roomTitle(currentRoom.name()));
            outputStream.println(currentRoom.description());
        }
    }

    private void runMainLoop(WorldDefinition worldDefinition, WorldState worldState) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = reader.readLine()) != null) {
            Command command = parser.parse(line);

            if ("SAVE".equals(command.action())) {
                handleSave(worldState);
                continue;
            }

            if ("LOAD".equals(command.action())) {
                outputStream.println("Load command needs a save path. Use --load=<path> when starting.");
                continue;
            }

            if ("QUIT".equals(command.action())) {
                break;
            }

            CommandResult result = gameEngine.tick(command, worldState, worldDefinition);

            if (result.disambiguation().isPresent()) {
                outputStream.println(result.output());
                outputStream.print("> ");
                String disambiguatedLine = reader.readLine();
                if (disambiguatedLine != null) {
                    Command disambiguatedCommand = parser.parse(disambiguatedLine);
                    result = gameEngine.tick(disambiguatedCommand, worldState, worldDefinition);
                } else {
                    break;
                }
            }

            outputStream.println(result.output());

            if (result.gameOver()) {
                break;
            }
        }
    }

    private void handleSave(WorldState worldState) {
        try {
            WorldStateDto stateDto = WorldStateDto.fromDomain(worldState);
            SaveGame saveGame = new SaveGame(SaveGame.FORMAT_VERSION, "default", stateDto, Instant.now());
            saveGameRepository.save(Path.of("save.json"), saveGame);
            outputStream.println("Game saved.");
        } catch (IOException e) {
            outputStream.println(palette.error("Failed to save game: " + e.getMessage()));
        }
    }
}
