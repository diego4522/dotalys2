package de.lighti.parsing;

import de.lighti.DefaultGameEventListener;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.state.ParseState;

public class ItemListener extends DefaultGameEventListener {
    private final AppState appState;

    public ItemListener( AppState state ) {
        super();
        appState = state;
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getDtName().contains( "Item" )) {
//            System.out.println( e.getId() + "->" + e.getEntityClass().getDtName() );
//            for (final Property p : e.getProperties()) {
//                System.out.println( p.getName() + "->" + p.getValue() );
//            }
            if (((String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() != null)
                            && (!((String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue()).isEmpty())) {
                appState.addItem( e.getId(), (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
            }
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        if (e.getEntityClass().getDtName().contains( "Item" )) {
//          System.out.println( e.getId() + "->" + e.getEntityClass().getDtName() );
//          for (final Property p : e.getProperties()) {
//              System.out.println( p.getName() + "->" + p.getValue() );
//          }
            if (((String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() != null)
                            && (!((String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue()).isEmpty())) {
                appState.addItem( e.getId(), (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
            }
        }
    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {

    }

}
