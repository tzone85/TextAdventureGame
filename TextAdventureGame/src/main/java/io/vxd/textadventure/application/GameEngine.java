package io.vxd.textadventure.application;

import io.vxd.textadventure.application.handlers.*;
import io.vxd.textadventure.domain.*;
import java.util.*;

public class GameEngine {
    private final Map<String, CommandHandler> handlers;
    private final EventEngine eventEngine;
    private final PredicateEvaluator predicateEvaluator;

    public GameEngine() {
        this.eventEngine = new EventEngine();
        this.predicateEvaluator = new PredicateEvaluator();
        this.handlers = initializeHandlers();
    }

    private Map<String, CommandHandler> initializeHandlers() {
        Map<String, CommandHandler> map = new HashMap<>();
        map.put(Command.ACTION_LOOK, new LookHandler());
        map.put(Command.ACTION_TAKE, new TakeHandler());
        map.put(Command.ACTION_GO, new GoHandler());
        map.put(Command.ACTION_INVENTORY, new InventoryHandler());
        map.put(Command.ACTION_USE, new UseHandler());
        map.put(Command.ACTION_TALK, new TalkHandler());
        map.put(Command.ACTION_PUT, new PutHandler());
        return map;
    }

    public CommandResult tick(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        CommandHandler handler = handlers.get(command.action());
        if (handler == null) {
            return new CommandResult("Unknown command: " + command.action());
        }

        CommandResult result = handler.handle(command, worldState, worldDefinition);

        // Fire events based on the action
        eventEngine.fireEvents(command, worldState, worldDefinition);

        // Check endings
        CommandResult finalResult = checkEndings(result, worldState, worldDefinition);

        return finalResult;
    }

    private CommandResult checkEndings(CommandResult result, WorldState worldState, WorldDefinition worldDefinition) {
        if (result.gameOver()) {
            return result;
        }

        for (Ending ending : worldDefinition.endings()) {
            if (ending.predicate().test(worldState)) {
                return new CommandResult(
                    result.output() + "\n" + ending.epilogue(),
                    true,
                    ending.id()
                );
            }
        }

        return result;
    }
}
