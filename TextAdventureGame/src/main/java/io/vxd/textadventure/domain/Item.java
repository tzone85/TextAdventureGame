package io.vxd.textadventure.domain;

import java.util.*;

public record Item(String id, List<String> names, boolean takeable, boolean container, List<String> contentIds, int weight) {
    public Item {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(names, "names cannot be null");
        Objects.requireNonNull(contentIds, "contentIds cannot be null");
        names = Collections.unmodifiableList(new ArrayList<>(names));
        contentIds = Collections.unmodifiableList(new ArrayList<>(contentIds));
    }

    public boolean matchesName(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        return names.stream().anyMatch(name -> name.toLowerCase().equals(lowerInput));
    }

    @Override
    public List<String> names() {
        return names;
    }

    @Override
    public List<String> contentIds() {
        return contentIds;
    }
}
