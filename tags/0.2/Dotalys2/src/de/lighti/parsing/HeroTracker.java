package de.lighti.parsing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import de.lighti.DefaultGameEventListener;
import de.lighti.DotaPlay;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.Property;
import de.lighti.model.game.Ability;
import de.lighti.model.game.Hero;
import de.lighti.model.state.ParseState;

public class HeroTracker extends DefaultGameEventListener {
    private final AppState state;

    private final Map<Hero, TreeMap<Long, Integer[]>> itemCache;

    public HeroTracker( AppState state ) {
        super();
        this.state = state;
        itemCache = new HashMap<Hero, TreeMap<Long, Integer[]>>();
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().contains( "CDOTA_Unit_Hero_" )) {

            if (state.getHero( e.getId() ) == null) {
                state.setHero( e.getId(), new Hero( AppState.getHeroName( e.getEntityClass().getName() ) ) );
            }
            for (final Property p : e.getProperties()) {
                final String name = p.getName();
                if (name.contains( "m_hAbilities" )) {

                    int value = (int) e.getProperty( name ).getValue();
                    if (value != 0x1FFFFF) {
                        final Hero h = state.getHero( e.getId() );
                        final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                        value &= 0x7ff;

//                        h.addAbility( DotaPlay.getTickMs(), slot, value );
                        final Ability a = state.getAbility( value );
                        if (a == null) {
                            Logger.getLogger( getClass().getName() ).warning( "Hero " + h.getName() + " has an odd ability" ); //Most likely a temporary entity
                        }
                        else {
                            h.getAbilities().add( a );
                        }
                    }
                }

            }
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        if (e.getEntityClass().getDtName().contains( "DT_DOTA_Unit_Hero" )) {
            final Hero h = state.getHero( e.getId() );
            if (name.equals( "DT_DOTA_BaseNPC.m_cellX" )) {
                state.getHero( e.getId() ).addX( tickMs, (Integer) e.getProperty( name ).getValue() );
            }
            else if (name.equals( "DT_DOTA_BaseNPC.m_cellY" )) {
                state.getHero( e.getId() ).addY( tickMs, (Integer) e.getProperty( name ).getValue() );
            }
            else if (name.contains( "m_hItems" )) {
                final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                int value = (int) e.getProperty( name ).getValue();
                if (value != 0x1FFFFF) {
                    value &= 0x7ff;
//                    h.setItem( DotaPlay.getTickMs(), slot, value );
                    setItemInCache( h, DotaPlay.getTickMs(), slot, value );
                }
                else {
//                    h.setItem( DotaPlay.getTickMs(), slot, null );
                    setItemInCache( h, DotaPlay.getTickMs(), slot, null );
                }
            }
            else if (name.contains( "m_hAbilities" )) {
                int value = (int) e.getProperty( name ).getValue();
                if (value != 0x1FFFFF) {

                    final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                    value &= 0x7ff;
                    final Ability a = state.getAbility( value );
                    if (a == null) {
                        Logger.getLogger( getClass().getName() ).warning( "Hero " + h.getName() + " has an odd ability" ); //Most likely a temporary entity
                    }
                    else {
                        h.getAbilities().add( a );
                    }
                }
            }
            else if (name.equals( "DT_DOTA_BaseNPC.m_iHealth" )) {
                final Integer value = (Integer) e.getProperty( name ).getValue();
                if (value == 0) {
                    final int x = (Integer) e.getProperty( "DT_DOTA_BaseNPC.m_cellX" ).getValue();
                    final int y = (Integer) e.getProperty( "DT_DOTA_BaseNPC.m_cellY" ).getValue();
                    h.addDeath( DotaPlay.getTickMs(), x, y );
                }
            }
        }
    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        super.parseComplete( tickMs, state );

        for (final Hero h : itemCache.keySet()) {
            final TreeMap<Long, Integer[]> heroItems = itemCache.get( h );
            for (final Long l : heroItems.keySet()) {
                final Integer[] frameItems = heroItems.get( l );
                for (int slot = 0; slot < frameItems.length; slot++) {
                    if (frameItems[slot] != null) {
                        h.setItem( l, slot, this.state.getItem( l, frameItems[slot] ) );
                    }
                    else {
                        h.setItem( l, slot, null );
                    }
                }
            }
        }

        itemCache.clear();
    }

    private void setItemInCache( Hero h, long tickMs, int slot, Integer value ) {
        TreeMap<Long, Integer[]> items = itemCache.get( h );
        if (items == null) {
            items = new TreeMap<Long, Integer[]>();
            items.put( 0l, new Integer[Hero.BAG_SIZE] );
            itemCache.put( h, items );
        }
        if (items.containsKey( tickMs )) {
            //Just store the update
            items.get( tickMs )[slot] = value;
        }
        else {
            //We advanced. Push the current bag configuration, calculate the diff to the previous one and make
            //a new array for the new tick
            final Entry<Long, Integer[]> current = items.floorEntry( tickMs );

            final Integer[] newBag = Arrays.copyOf( current.getValue(), current.getValue().length );
            newBag[slot] = value;
            items.put( tickMs, newBag );

        }
    }

}
