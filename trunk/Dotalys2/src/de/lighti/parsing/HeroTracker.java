package de.lighti.parsing;

import de.lighti.DefaultGameEventListener;
import de.lighti.DotaPlay;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.Property;
import de.lighti.model.game.Hero;

public class HeroTracker extends DefaultGameEventListener {
    private final AppState state;

    private final int o = 0;

    public HeroTracker( AppState state ) {
        super();
        this.state = state;

    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().contains( "CDOTA_Unit_Hero_" )) {
//          System.out.println( e.getId() + " " + e.getEntityClass().getName() );
            if (state.getHero( e.getId() ) == null) {
                state.setHero( e.getId(), new Hero( e.getEntityClass().getName() ) );
            }
            for (final Property p : e.getProperties()) {
                final String name = p.getName();
                if (e.getEntityClass().getName().contains( "Ursa" ) && name.contains( "m_hItems" )) {

                }
                else if (name.contains( "m_hAbilities" )) {
//                    System.out.println( e.getEntityClass().getDtName() );
                    int value = (int) e.getProperty( name ).getValue();
                    if (value != 0x1FFFFF) {
                        final Hero h = state.getHero( e.getId() );
                        final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                        value &= 0x7ff;
//                    System.out.println( e.getEntityClass().getDtName() + e.getId() + " " + slot + "->" + value );
                        h.addAbility( DotaPlay.getTickMs(), slot, value );
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
                    h.setItem( DotaPlay.getTickMs(), slot, state.getItem( value ).getName() );
                }
                else {
                    h.setItem( DotaPlay.getTickMs(), slot, null );
                }
            }
            else if (name.contains( "m_hAbilities" )) {
                int value = (int) e.getProperty( name ).getValue();
                if (value != 0x1FFFFF) {

                    final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
                    value &= 0x7ff;
//                System.out.println( e.getEntityClass().getDtName() + e.getId() + " " + slot + "->" + value );
                    h.addAbility( DotaPlay.getTickMs(), slot, value );
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
//            else if (e.getEntityClass().getDtName().contains( "Ursa" )) {
//                System.out.println( e.getId() + "." + name + " " + oldValue + "->" + e.getProperty( name ).getValue() );
//            }
        }
    }

}
