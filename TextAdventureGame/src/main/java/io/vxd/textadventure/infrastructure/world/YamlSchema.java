package io.vxd.textadventure.infrastructure.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class YamlSchema {

    public static record WorldDto(
        @JsonProperty("start_room") String startRoom,
        @JsonProperty("start_inventory") List<String> startInventory,
        Map<String, RoomDto> rooms,
        Map<String, ItemDto> items,
        Map<String, NpcDto> npcs,
        List<EventDto> events,
        List<EndingDto> endings
    ) {
    }

    public static record RoomDto(
        String id,
        String title,
        String description,
        Map<String, String> exits
    ) {
    }

    public static record ItemDto(
        String id,
        String name,
        String description,
        @JsonProperty("is_container") Boolean isContainer
    ) {
    }

    public static record NpcDto(
        String id,
        String name,
        String description
    ) {
    }

    public static record EventDto(
        String id,
        String trigger,
        List<PredicateDto> predicates,
        String action
    ) {
    }

    public static record PredicateDto(
        String type,
        String value
    ) {
    }

    public static record EndingDto(
        String id,
        String type,
        String message
    ) {
    }
}
