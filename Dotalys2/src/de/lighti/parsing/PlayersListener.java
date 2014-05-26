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
import de.lighti.model.state.ParseState;

public class PlayersListener extends DefaultGameEventListener {
    private final AppState state;

    private final Pattern playerPattern;

    private final Map<String, Player> playerBuffer;

    public PlayersListener( AppState state ) {
        super();
        this.state = state;
        playerPattern = Pattern.compile( "\\.[0-9][0-9][0-9][0-9]$" );
        playerBuffer = new HashMap();
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
            Player p = playerBuffer.get( id );
            if (p == null) {
                playerBuffer.put( id, new Player( id, "<unknown>" ) );
                p = playerBuffer.get( id );
            }

            boolean handled = false;

            if (name.contains( "m_iszPlayerNames" )) {
                p.setName( (String) value );
                handled = true;
            }

            if (name.contains( "m_iTotalEarnedGold" )) {
                p.setTotalEarnedGold( (Integer) value );

            }
            else if (name.contains( "m_iTotalEarnedXP" )) {
                p.setTotalXP( (Integer) value );
            }

            else if (name.contains( "m_hSelectedHero" )) {
                p.setHero( state.getHero( (Integer) value & 0x7FF ) );
                handled = true;
            }
            if (!handled) {
                state.addPlayerVariable( name.substring( 0, name.lastIndexOf( "." ) ) );
            }
        }

    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        for (final Player p : playerBuffer.values()) {
            if (p.getHero() != null) {
                this.state.addPlayer( p.getName(), p );
            }
        }
    }

}
