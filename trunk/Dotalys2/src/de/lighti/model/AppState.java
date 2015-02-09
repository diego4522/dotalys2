package de.lighti.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import de.lighti.model.game.Ability;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;

public class AppState {
    public enum GameState {
        DOTA_GAMERULES_STATE_DISCONNECT,
        /**
         * This state is reached after the 1 minute game time mark, when the horn sounds
         */
        DOTA_GAMERULES_STATE_GAME_IN_PROGRESS,

        /**
         * After loading is complete, the game switches into whatever hero selection mode this game has
         */
        DOTA_GAMERULES_STATE_HERO_SELECTION, DOTA_GAMERULES_STATE_INIT, DOTA_GAMERULES_STATE_LAST, DOTA_GAMERULES_STATE_POST_GAME, DOTA_GAMERULES_STATE_PRE_GAME, DOTA_GAMERULES_STATE_STRATEGY_TIME, DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD;

        /**
         * These values are taken from the game's DOTA_GameState struct
         * @param id
         * @return
         */
        public static GameState fromInternal( int id ) {
            switch (id) {
                case 7:
                    return DOTA_GAMERULES_STATE_DISCONNECT;
                case 5:
                    return DOTA_GAMERULES_STATE_GAME_IN_PROGRESS;
                case 2:
                    return DOTA_GAMERULES_STATE_HERO_SELECTION;
                case 0:
                    return DOTA_GAMERULES_STATE_INIT;
                case 9:
                    return DOTA_GAMERULES_STATE_LAST;
                case 6:
                    return DOTA_GAMERULES_STATE_POST_GAME;
                case 4:
                    return DOTA_GAMERULES_STATE_PRE_GAME;
                case 3:
                    return DOTA_GAMERULES_STATE_STRATEGY_TIME;
                case 1:
                    return DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public static String getAbilityName( String key ) {
        if (key != null) {

            if (abilityNames.containsKey( key.toUpperCase() )) {
                return abilityNames.get( key.toUpperCase() );
            }
            else {
                return key;
            }
        }
        else {
            return null;
        }
    }

    public static String getHeroName( String className ) {
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

    public static void setAbilityName( String name, String localisedName ) {
        if (name != null && localisedName != null) {
            abilityNames.put( name.toUpperCase(), localisedName );
        }
    }

    public static void setHeroName( String className, String localisedName ) {
        if (className != null && localisedName != null) {
            heroNames.put( className.toUpperCase(), localisedName );
        }
    }

    private final static Logger LOGGER = Logger.getLogger( AppState.class.getName() );

    public TreeMap<Long, Map<String, Object>> gameEventsPerMs = new TreeMap<Long, Map<String, Object>>();
    //    private final SortedMap<String, Player> players = new TreeMap<String, Player>();
    private final Set<Player> players;

    private final Set<String> playerVariables;
    private final Map<Integer, Hero> heroes;
    private final TreeMap<Long, Map<Integer, Dota2Item>> items;
    private final TreeMap<Long, Map<Integer, Ability>> abilities;
    private final TreeMap<GameState, Long> gameStateChanges;

    private final static Map<String, String> heroNames;

    private final static Map<String, String> abilityNames;

    private int msPerTick;

    static {
        heroNames = new HashMap<String, String>();
        abilityNames = new HashMap<String, String>();
    }

    public AppState() {
        playerVariables = new HashSet<String>();
        heroes = new HashMap<Integer, Hero>();
        items = new TreeMap<Long, Map<Integer, Dota2Item>>();
        items.put( 0l, new HashMap<Integer, Dota2Item>() );
        abilities = new TreeMap<Long, Map<Integer, Ability>>();
        abilities.put( 0l, new HashMap<Integer, Ability>() );
        players = new HashSet<Player>();
        gameStateChanges = new TreeMap<GameState, Long>();
        clear();
    }

    public void addAbility( long tick, int id, String ability ) {
        Map<Integer, Ability> i = abilities.get( tick );
        if (i == null) {
            i = new HashMap<Integer, Ability>( abilities.floorEntry( tick ).getValue() );
            abilities.put( tick, i );
        }
        i.put( id, new Ability( ability ) );

    }

    public void addGameStateChange( GameState s, Long l ) {
        if (gameStateChanges.containsKey( s ) && gameStateChanges.get( s ) < l) {
            LOGGER.warning( "We already seen state " + s + " with a different time mark " + l
                            + ". not sure if the game can cycle through seen states. Discarding new value." );
            return;
        }
        gameStateChanges.put( s, l );
    }

    public void addItem( long tick, int id, String value ) {
        Map<Integer, Dota2Item> i = items.get( tick );
        if (i == null) {
            i = new HashMap<Integer, Dota2Item>( items.floorEntry( tick ).getValue() );
            items.put( tick, i );
        }
        i.put( id, new Dota2Item( value ) );

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
        items.put( 0l, new HashMap<Integer, Dota2Item>() );
        abilities.clear();
        abilities.put( 0l, new HashMap<Integer, Ability>() );
        players.clear();
        gameStateChanges.clear();
        gameStateChanges.put( GameState.DOTA_GAMERULES_STATE_INIT, 0l );
    }

    public Ability getAbility( long tick, int value ) {
        if (abilities.isEmpty()) {
            return Ability.UNKNOWN_ABILITY;
        }

        final Entry<Long, Map<Integer, Ability>> e = abilities.floorEntry( tick );
        if (e.getValue().containsKey( value )) {
            return e.getValue().get( value );
        }
        else {
            return Ability.UNKNOWN_ABILITY;
        }
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

    /**
     * Returns the ms (in replay time) when a certain game state was entered.
     *
     * @param s the game state
     * @return the time since server start, null if that game state was never reached.
     */
    public Long getGameStateTime( GameState s ) {
        return gameStateChanges.get( s );
    }

    public Hero getHero( int value ) {
        return heroes.get( value );
    }

    /**
     * The horn time in replay time (ms)
     * @return aka game start time (one minute after hero spawn)
     */
    public long getHornTime() {
        return getGameStateTime( GameState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS );
    }

    /**
     * Returns the Dota2Item corresponding to a (entity) id. The values in a
     * heroe's h_mItems array correspond to volatile entity ids representing the game item.
     * Hence we have to track the timestamp when a certain id was assigned to an item.
     * @param tick the timestamp
     * @param value the entity id of the corresponding CDOTA_Item entity
     * @return the Dota2Item
     */
    public Dota2Item getItem( long tick, int value ) {
        if (items.isEmpty()) {
            return Dota2Item.UNKNOWN_ITEM;
        }
        final Entry<Long, Map<Integer, Dota2Item>> e = items.floorEntry( tick );
        if (e.getValue().containsKey( value )) {
            return e.getValue().get( value );
        }
        else {
            return Dota2Item.UNKNOWN_ITEM;
        }
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

    public void removeAbility( long tick, int id ) {
        Map<Integer, Ability> i = abilities.get( tick );
        if (i == null) {
            i = new HashMap<Integer, Ability>( abilities.floorEntry( tick ).getValue() );
            abilities.put( tick, i );
        }
        i.remove( id );
    }

    public void removeItem( long tick, int id ) {
        Map<Integer, Dota2Item> i = items.get( tick );
        if (i == null) {
            i = new HashMap<Integer, Dota2Item>( items.floorEntry( tick ).getValue() );
            items.put( tick, i );
        }
        i.remove( id );

    }

    public void setHero( int id, Hero hero ) {
        heroes.put( id, hero );
    }

    public void setMsPerTick( int msPerTick ) {
        this.msPerTick = msPerTick;
    }

}
