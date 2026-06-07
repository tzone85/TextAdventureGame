package io.vxd.textadventure.infrastructure.world;

public class WorldValidationException extends RuntimeException {
    public WorldValidationException(String message) {
        super(message);
    }

    public WorldValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
