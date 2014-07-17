package de.lighti.parsing;

import de.lighti.DefaultGameEventListener;
import de.lighti.DotaPlay;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.game.Ability;

public class AbilityTracker extends DefaultGameEventListener {
    private final AppState state;

    public AbilityTracker( AppState appState ) {
        state = appState;
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        super.entityCreated( tickMs, e );
        final String dtName = e.getEntityClass().getDtName();
        if (dtName.contains( "DOTA_Ability" ) || dtName.contains( "DOTABaseAbility" )) {
//            System.err.println( e.getId() + "->" + e.getEntityClass().getDtName() );
            if (state.getAbility( e.getId() ) == null) {
                state.addAbility( e.getId(), new Ability( (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() ) );
            }
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        final String dtName = e.getEntityClass().getDtName();
        if (dtName.contains( "DOTA_Ability" ) || dtName.contains( "DOTABaseAbility" )) {
            final Ability a = state.getAbility( e.getId() );
            if (name.contains( "m_iName" )) {
                a.setKey( (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
            }
            else if (name.contains( "m_iLevel" )) {
                a.setLevel( DotaPlay.getTickMs(), (Integer) e.getProperty( name ).getValue() );
//                if (((String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue()).contains( "sniper" )) {
//                    System.err.println( e.getId() + " " + e.getProperty( "DT_BaseEntity.m_iName" ).getValue() + " " + name + "->"
//                                    + e.getProperty( name ).getValue() );
//                }
            }
            else if (name.equals( "DT_DOTABaseAbility.m_bInAbilityPhase" )) {
//              if (state.getAbility( e.getId() ) != null) {
//              System.out.println( "I am ability " + state.getAbility( e.getId() ) );
//              }
//              if (state.getHero( owner ) != null) {
//                  System.out.println( "I belong to " + state.getHero( owner ).getName() );
//              }
//                  System.out.p0 rintln( e.getEntityClass().getName() );
                final Integer value = (Integer) e.getProperty( name ).getValue();
                if ((Integer) oldValue == 0 && value > 0) {
//                      System.out.println( "I am ability " + state.getAbility( e.getId() ) );
                    ////
//                      System.out.println( name + ": " + oldValue + " -> " + e.getProperty( name ) );
//                      System.out.println( tickMs + " -> " + state.getAbility( e.getId() ) );
                    state.getAbility( e.getId() ).addInvocation( tickMs );
                }
            }

        }
    }

}
