package de.lighti.parsing;

import de.lighti.DefaultGameEventListener;
import de.lighti.model.AppState;
import de.lighti.model.Entity;
import de.lighti.model.Statics;
import de.lighti.model.game.Dota2Item;

public class ItemTracker extends DefaultGameEventListener {
    private final AppState appState;

    public ItemTracker( AppState state ) {
        super();
        appState = state;
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().startsWith( "CDOTA_Item" )) {
            final String key = (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue();
            if (!key.isEmpty() && !key.startsWith( "item" ) && key != Statics.UNKNOWN_ITEM) {
                throw new IllegalArgumentException( key + ": an item that doesn't start with \"item\" is probably not an item" );
            }
            appState.addItem( tickMs, e.getId(), (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
        }
    }

    @Override
    public void entityRemoved( long tickMs, Entity removed ) {
        super.entityRemoved( tickMs, removed );
        appState.removeItem( tickMs, removed.getId() );
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        final Dota2Item item = appState.getItem( tickMs, e.getId() );
        if (item != Dota2Item.UNKNOWN_ITEM) {
            if (name.equals( "DT_BaseEntity.m_iName" )) {
                item.setKey( (String) e.getProperty( "DT_BaseEntity.m_iName" ).getValue() );
            }
            else if (name.equals( "DT_DOTABaseAbility.m_fCooldown" )) {
                final float coolcownEnd = (float) e.getProperty( "DT_DOTABaseAbility.m_fCooldown" ).getValue();
                final float cooldown = (float) e.getProperty( "DT_DOTABaseAbility.m_flCooldownLength" ).getValue();
                if (cooldown > 0) {
                    //Cooldown stamps are in seconds. Don't forget to multiply by 1000
                    item.addUsage( (long) ((coolcownEnd - cooldown) * 1000l) );
                }
            }
        }
    }

}
