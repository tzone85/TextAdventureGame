package io.vxd.textadventure.application.parser;

import java.util.Optional;

public record Command(
    Verb verb,
    Optional<String> directObject,
    Optional<String> indirectObject,
    String raw
) {}
