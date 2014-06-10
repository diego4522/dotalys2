package de.lighti.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.lighti.model.game.Ability;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;

public class AppState {
    public TreeMap<Long, Map<String, Object>> gameEventsPerMs = new TreeMap<Long, Map<String, Object>>();
//    private final SortedMap<String, Player> players = new TreeMap<String, Player>();
    private final Set<Player> players;

    private final Set<String> playerVariables;
    private final Map<Integer, Hero> heroes;
    private final Map<Integer, Dota2Item> items;
    private final Map<Integer, Ability> abilities;

    private final Map<String, String> heroNames;

    private int msPerTick;

    public AppState() {
        playerVariables = new HashSet<String>();
        heroes = new HashMap<Integer, Hero>();
        items = new HashMap<Integer, Dota2Item>();
        abilities = new HashMap<Integer, Ability>();
        heroNames = new HashMap<String, String>();
        players = new HashSet<Player>();
    }

    public void addAbility( int id, Ability ability ) {
        abilities.put( id, ability );

    }

    public void addItem( int id, String value ) {
        items.put( id, new Dota2Item( value ) );

    }

    public void addPlayer( Player p ) {
        players.add( p );

    }

    public void addPlayerVariable( String n ) {
        if (!playerVariables.contains( n )) {
            playerVariables.add( n );
        }
    }

    public void clear() {
        playerVariables.clear();
        heroes.clear();
        items.clear();
        abilities.clear();
        players.clear();
    }

    public Ability getAbility( int id ) {
        return abilities.get( id );
    }

    /**
     * Returns the replay's length in miliseconds. Please
     * be aware that the this is the timestamp when the replay ended,
     * not when the game was decided, i.e. the throne was hit.
     * 
     * @return the game's length in miliseconds
     */
    public long getGameLength() {
        return gameEventsPerMs.lastKey();
    }

    public Hero getHero( int value ) {
        return heroes.get( value );
    }

    public Hero getHeroForPlayer( String playerName ) {
        final Player p = getPlayerByName( playerName );
        return p.getHero();
    }

    public String getHeroName( String className ) {
        if (className != null) {
            String actualClassName = className.toUpperCase();
            actualClassName = actualClassName.replace( "CDOTA_UNIT", "NPC_DOTA" );
            if (heroNames.containsKey( actualClassName )) {
                return heroNames.get( actualClassName );
            }
            else {
                return className;
            }
        }
        else {
            return className;
        }
    }

    public Dota2Item getItem( int value ) {
        if (items.containsKey( value )) {
            return items.get( value );
        }
        else {
            return Dota2Item.UNKNOWN_ITEM;
        }
    }

    public Dota2Item getItemByName( String name ) {
        for (final Dota2Item d : items.values()) {
            if (d.getName().equals( name )) {
                return d;
            }
        }
        return Dota2Item.UNKNOWN_ITEM;
    }

    public Object getLastKnownValue( long tick, String name ) {
        Map.Entry<Long, Map<String, Object>> e = gameEventsPerMs.floorEntry( tick );
        while (e != null) {
            if (e.getValue().containsKey( name )) {
                return e.getValue().get( name );

            }
            else {
                e = gameEventsPerMs.lowerEntry( e.getKey() );
            }

        }
        return null;
    }

    public int getMsPerTick() {
        return msPerTick;
    }

    public Player getPlayer( int id ) {
        for (final Player p : players) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new IllegalArgumentException( "no such player" );
    }

    public Player getPlayerByName( String player ) {
        for (final Player p : players) {
            if (p.getName().equals( player )) {
                return p;
            }
        }
        return null;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Set<String> getUnhandledPlayerVariableNames() {
        return playerVariables;
    }

    public void setHero( int id, Hero hero ) {
        heroes.put( id, hero );
    }

    public void setHeroName( String className, String localisedName ) {
        if (className != null && localisedName != null) {
            heroNames.put( className.toUpperCase(), localisedName );
        }
    }

    public void setMsPerTick( int msPerTick ) {
        this.msPerTick = msPerTick;
    }

}
