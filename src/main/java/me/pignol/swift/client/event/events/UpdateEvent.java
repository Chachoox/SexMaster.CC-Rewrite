package me.pignol.swift.client.event.events;

import me.pignol.swift.client.event.EventStageable;
import me.pignol.swift.client.event.Stage;

public class UpdateEvent extends EventStageable {

    public UpdateEvent(Stage stage) {
        super(stage);
    }
}


