package io.vxd.textadventure.infrastructure.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.vxd.textadventure.domain.world.WorldDefinition;
import java.nio.file.Path;

public class WorldLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public static WorldDefinition load(String path) {
        return load(Path.of(path));
    }

    public static WorldDefinition load(Path path) {
        try {
            YamlSchema.WorldDto worldDto = MAPPER.readValue(path.toFile(), YamlSchema.WorldDto.class);
            return WorldMapper.mapToWorldDefinition(worldDto);
        } catch (Exception e) {
            throw new WorldValidationException("Failed to load world: " + e.getMessage(), e);
        }
    }
}
