package de.lighti.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lighti.DefaultGameEventListener;
import de.lighti.DotaPlay;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.Property;
import de.lighti.model.game.Player;

public class PlayersListener extends DefaultGameEventListener {
    private final AppState state;

    private final Pattern playerPattern;

    public PlayersListener( AppState state ) {
        super();
        this.state = state;
        playerPattern = Pattern.compile( "\\.[0-9][0-9][0-9][0-9]$" );
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().equals( "CDOTA_PlayerResource" )) {
            Map<String, Object> tickMap = state.gameEventsPerMs.get( DotaPlay.getTickMs() );
            if (tickMap == null) {
                tickMap = new HashMap<String, Object>();
                state.gameEventsPerMs.put( DotaPlay.getTickMs(), tickMap );
            }
            for (final Property<?> p : e.getProperties()) {
                final String name = p.getName();
                tickMap.put( name, p.getValue() );

                handleWorldVar( name, p.getValue() );

            }

        }

    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {

        if (e.getEntityClass().getName().equals( "CDOTA_PlayerResource" )) {
            Map<String, Object> tickMap = state.gameEventsPerMs.get( DotaPlay.getTickMs() );
            if (tickMap == null) {
                tickMap = new HashMap<String, Object>();
                state.gameEventsPerMs.put( DotaPlay.getTickMs(), tickMap );
            }

            handleWorldVar( name, e.getProperty( name ).getValue() );
            tickMap.put( name, e.getProperty( name ).getValue() );

        }

    }

    private void handleWorldVar( String name, Object value ) {

        final Matcher m = playerPattern.matcher( name );
        if (m.find()) {

            final String id = name.substring( name.lastIndexOf( "." ) );
            final Player p = state.getPlayer( id );
            boolean handled = p == null;
            if (name.contains( "m_iszPlayerNames" ) && !((String) value).isEmpty()) {
                if (state.getPlayer( id ) == null) {
                    state.addPlayer( id, new Player( id, (String) value ) );
                }
                handled = true;
            }
            else if (name.contains( "m_iTotalEarnedGold" )) {
                if (p != null) {
                    state.getPlayer( id ).setTotalEarnedGold( (Integer) value );
                }
            }
            else if (name.contains( "m_iTotalEarnedXP" )) {
                if (p != null) {
                    state.getPlayer( id ).setTotalXP( (Integer) value );
                }
            }

            else if (name.contains( "m_hSelectedHero" )) {
                if (p != null) {
                    p.setHero( state.getHero( ((Integer) value & 0x7FF) ) );
                }
                handled = true;
            }
            if (!handled) {
                state.addPlayerVariable( name.substring( 0, name.lastIndexOf( "." ) ) );
            }
        }

    }

}
