package de.lighti.parsing;

import de.lighti.DefaultGameEventListener;
import de.lighti.model.AppState;
import de.lighti.model.state.ParseState;

public class GeneralGameStateTracker extends DefaultGameEventListener {
    private final AppState appState;

    public GeneralGameStateTracker( AppState appState ) {
        this.appState = appState;
    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        appState.setMsPerTick( (int) (state.getTickInterval() * 1000) );
    }

}
