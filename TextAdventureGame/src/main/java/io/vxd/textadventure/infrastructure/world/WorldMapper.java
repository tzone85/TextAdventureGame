package io.vxd.textadventure.infrastructure.world;

import io.vxd.textadventure.domain.world.*;
import java.util.*;

public class WorldMapper {

    public static WorldDefinition mapToWorldDefinition(YamlSchema.WorldDto dto) {
        validateStartRoomExists(dto);

        Map<String, Room> rooms = mapRooms(dto.rooms());
        Map<String, Item> items = mapItems(dto.items());
        Map<String, Npc> npcs = mapNpcs(dto.npcs());
        List<WorldEvent> events = mapEvents(dto.events());
        List<Ending> endings = mapEndings(dto.endings());

        validateExitReferencesKnownRoom(rooms);
        validateItemIdsAreUnique(items);
        validateEventsReferencedTriggersExist(events);
        validateEndingsGreaterThanZero(endings);

        return new WorldDefinition(
            dto.startRoom(),
            rooms,
            items,
            npcs,
            events,
            endings
        );
    }

    private static void validateStartRoomExists(YamlSchema.WorldDto dto) {
        if (dto.startRoom() == null || dto.startRoom().isBlank()) {
            throw new WorldValidationException("Field 'start_room' is required");
        }
        if (dto.rooms() == null || !dto.rooms().containsKey(dto.startRoom())) {
            throw new WorldValidationException("Field 'start_room': referenced room '" + dto.startRoom() + "' does not exist");
        }
    }

    private static Map<String, Room> mapRooms(Map<String, YamlSchema.RoomDto> roomDtos) {
        if (roomDtos == null) {
            return Collections.emptyMap();
        }
        Map<String, Room> rooms = new HashMap<>();
        for (Map.Entry<String, YamlSchema.RoomDto> entry : roomDtos.entrySet()) {
            YamlSchema.RoomDto roomDto = entry.getValue();
            Room room = new Room(
                roomDto.id(),
                roomDto.title(),
                roomDto.description(),
                roomDto.exits() != null ? roomDto.exits() : Collections.emptyMap()
            );
            rooms.put(entry.getKey(), room);
        }
        return rooms;
    }

    private static Map<String, Item> mapItems(Map<String, YamlSchema.ItemDto> itemDtos) {
        if (itemDtos == null) {
            return Collections.emptyMap();
        }
        Map<String, Item> items = new HashMap<>();
        for (Map.Entry<String, YamlSchema.ItemDto> entry : itemDtos.entrySet()) {
            YamlSchema.ItemDto itemDto = entry.getValue();
            Item item = new Item(
                itemDto.id(),
                itemDto.name(),
                itemDto.description(),
                itemDto.isContainer() != null && itemDto.isContainer()
            );
            items.put(entry.getKey(), item);
        }
        return items;
    }

    private static Map<String, Npc> mapNpcs(Map<String, YamlSchema.NpcDto> npcDtos) {
        if (npcDtos == null) {
            return Collections.emptyMap();
        }
        Map<String, Npc> npcs = new HashMap<>();
        for (Map.Entry<String, YamlSchema.NpcDto> entry : npcDtos.entrySet()) {
            YamlSchema.NpcDto npcDto = entry.getValue();
            Npc npc = new Npc(npcDto.id(), npcDto.name(), npcDto.description());
            npcs.put(entry.getKey(), npc);
        }
        return npcs;
    }

    private static List<WorldEvent> mapEvents(List<YamlSchema.EventDto> eventDtos) {
        if (eventDtos == null) {
            return Collections.emptyList();
        }
        List<WorldEvent> events = new ArrayList<>();
        for (YamlSchema.EventDto eventDto : eventDtos) {
            List<Predicate> predicates = eventDto.predicates() != null
                ? eventDto.predicates().stream()
                    .map(p -> new Predicate(p.type(), p.value()))
                    .toList()
                : Collections.emptyList();

            WorldEvent event = new WorldEvent(
                eventDto.id(),
                eventDto.trigger(),
                predicates,
                eventDto.action()
            );
            events.add(event);
        }
        return events;
    }

    private static List<Ending> mapEndings(List<YamlSchema.EndingDto> endingDtos) {
        if (endingDtos == null) {
            return Collections.emptyList();
        }
        List<Ending> endings = new ArrayList<>();
        for (YamlSchema.EndingDto endingDto : endingDtos) {
            Ending.EndingType type = Ending.EndingType.valueOf(endingDto.type().toUpperCase());
            Ending ending = new Ending(endingDto.id(), type, endingDto.message());
            endings.add(ending);
        }
        return endings;
    }

    private static void validateExitReferencesKnownRoom(Map<String, Room> rooms) {
        for (Room room : rooms.values()) {
            for (Map.Entry<String, String> exit : room.exits().entrySet()) {
                String targetRoomId = exit.getValue();
                if (!rooms.containsKey(targetRoomId)) {
                    throw new WorldValidationException(
                        "Field 'rooms." + room.id() + ".exits." + exit.getKey() + "': " +
                        "referenced room '" + targetRoomId + "' does not exist"
                    );
                }
            }
        }
    }

    private static void validateItemIdsAreUnique(Map<String, Item> items) {
        Set<String> seenIds = new HashSet<>();
        for (Item item : items.values()) {
            if (!seenIds.add(item.id())) {
                throw new WorldValidationException("Field 'items': duplicate item id '" + item.id() + "'");
            }
        }
    }

    private static void validateEventsReferencedTriggersExist(List<WorldEvent> events) {
        // For now, just validate that events have a trigger
        for (WorldEvent event : events) {
            if (event.trigger() == null || event.trigger().isBlank()) {
                throw new WorldValidationException("Field 'events': event '" + event.id() + "' must have a trigger");
            }
        }
    }

    private static void validateEndingsGreaterThanZero(List<Ending> endings) {
        if (endings.isEmpty()) {
            throw new WorldValidationException("Field 'endings': world must have at least one ending");
        }
    }
}
