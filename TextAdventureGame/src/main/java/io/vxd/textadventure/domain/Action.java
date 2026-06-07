package io.vxd.textadventure.domain;

import java.util.*;

public sealed interface Action permits Action.SetFlag, Action.MoveItem, Action.ChangeRoomDescription, Action.EndGame {

    record SetFlag(String flagName, boolean value) implements Action {
        public SetFlag {
            Objects.requireNonNull(flagName, "flagName cannot be null");
        }
    }

    record MoveItem(String itemId, String fromContainer, String toContainer) implements Action {
        public MoveItem {
            Objects.requireNonNull(itemId, "itemId cannot be null");
            Objects.requireNonNull(fromContainer, "fromContainer cannot be null");
            Objects.requireNonNull(toContainer, "toContainer cannot be null");
        }
    }

    record ChangeRoomDescription(String roomId, String newDescription) implements Action {
        public ChangeRoomDescription {
            Objects.requireNonNull(roomId, "roomId cannot be null");
            Objects.requireNonNull(newDescription, "newDescription cannot be null");
        }
    }

    record EndGame(String endingId) implements Action {
        public EndGame {
            Objects.requireNonNull(endingId, "endingId cannot be null");
        }
    }
}
