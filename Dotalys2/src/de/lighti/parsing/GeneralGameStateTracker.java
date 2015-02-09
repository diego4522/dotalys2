package de.lighti.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import de.lighti.DefaultGameEventListener;
import de.lighti.Dotalys2App;
import de.lighti.model.AppState.GameState;
import de.lighti.model.Entity;
import de.lighti.model.Property;
import de.lighti.model.Statics;
import de.lighti.model.game.Player;
import de.lighti.model.state.ParseState;

public class GeneralGameStateTracker extends DefaultGameEventListener {
    private final Dotalys2App app;
    private GameState lastSeenGameState;

    public GeneralGameStateTracker( Dotalys2App app ) {
        this.app = app;
        lastSeenGameState = GameState.DOTA_GAMERULES_STATE_INIT; //See comment in entityUpdated
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        super.entityUpdated( tickMs, e, name, oldValue );

        /*
         * The intended order of GameState the game is supposed to go through is:
         * DOTA_GAMERULES_STATE_INIT
         * DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD
         * DOTA_GAMERULES_STATE_HERO_SELECTION
         * DOTA_GAMERULES_STATE_PRE_GAME
         * DOTA_GAMERULES_STATE_GAME_IN_PROGRESS
         * DOTA_GAMERULES_STATE_POST_GAME
         * (You can see that if you turn up the verbosity in the game's console while playing))
         * For some reason unknown to me, the GameRulesProxy entity is cycling between DOTA_GAMERULES_STATE_INIT/DOTA_GAMERULES_STATE_GAME_IN_PROGRESS
         * while the game runs. To avoid log clutter, I only accept non-DOTA_GAMERULES_STATE_INIT states as new.
         *
         */
        if (tickMs > 0l && e.getEntityClass().getName().equals( "CDOTAGamerulesProxy" )) {
            final Property<Integer> p = e.getProperty( "DT_DOTAGamerules.m_nGameState" );
            final GameState s = GameState.fromInternal( p.getValue() );
            if (s != lastSeenGameState && s != GameState.DOTA_GAMERULES_STATE_INIT) {
                app.getAppState().addGameStateChange( s, tickMs );
                lastSeenGameState = s;
            }
        }
    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        if (state.getProtocolVersion() > Statics.SUPPORTED_PROTOCOL_VERSION) {
            JOptionPane.showMessageDialog( app, Statics.PROTOCOL_WARNING, Statics.WARNING, JOptionPane.WARNING_MESSAGE );
        }
        app.getAppState().setMsPerTick( (int) (state.getTickInterval() * 1000) );

        //Unhandled PlayerVariables
        final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) app.getHistogramComponent().getAttributeBox().getModel();
        for (final String s : app.getAppState().getUnhandledPlayerVariableNames()) {
            model.addElement( s );
        }

        //Players
        final DefaultListModel<String> playerHistogramModel = (DefaultListModel<String>) app.getHistogramComponent().getPlayerBox().getModel();
        final DefaultComboBoxModel<String> playerModel = (DefaultComboBoxModel<String>) app.getPlayerComponent().getPlayerBox().getModel();
        final List<Player> sortedPlayers = new ArrayList<Player>( app.getAppState().getPlayers() );
        Collections.sort( sortedPlayers, new Comparator<Player>() {

            @Override
            public int compare( Player o1, Player o2 ) {
                return Integer.compare( o1.getId(), o2.getId() );
            }
        } );
        for (final Player p : sortedPlayers) {
            playerHistogramModel.addElement( p.getName() );
            playerModel.addElement( p.getName() );
        }
        app.getMapComponent().buildTreeNodes( sortedPlayers );

        //Now fire up the application
        app.getMainView().setEnabled( true );

        //Clean up
        lastSeenGameState = null;

    }

}
