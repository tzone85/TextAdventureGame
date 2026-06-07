package io.vxd.textadventure.domain;

import java.util.*;

public record ConversationNode(String text, Map<String, ConversationNode> options) {
    public ConversationNode {
        Objects.requireNonNull(text, "text cannot be null");
        Objects.requireNonNull(options, "options cannot be null");
        options = Collections.unmodifiableMap(new HashMap<>(options));
    }

    @Override
    public Map<String, ConversationNode> options() {
        return options;
    }
}
