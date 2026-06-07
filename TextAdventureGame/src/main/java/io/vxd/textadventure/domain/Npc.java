package io.vxd.textadventure.domain;

import java.util.*;

public record Npc(String id, String name, String idleDialogue, ConversationNode conversationTree) {
    public Npc {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(idleDialogue, "idleDialogue cannot be null");
        Objects.requireNonNull(conversationTree, "conversationTree cannot be null");
    }
}
