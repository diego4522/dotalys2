package de.lighti.parsing;

import de.lighti.DefaultGameEventListener;
import de.lighti.model.AppState;
import de.lighti.model.Entity;

public class ItemListener extends DefaultGameEventListener {
    private final AppState appState;

    public ItemListener( AppState state ) {
        super();
        appState = state;
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getDtName().contains( "Item" )) {
            appState.addItem( tickMs, e.getId(), (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        if (e.getEntityClass().getDtName().contains( "Item" )) {
            if (name.equals( "DT_BaseEntity.m_iName" )) {
                appState.getItem( tickMs, e.getId() ).setName( (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
            }
        }
    }

}
