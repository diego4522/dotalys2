package de.lighti.components.player.statistics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.swing.JPanel;

import de.lighti.io.ImageCache;
import de.lighti.model.game.Ability;

public class SkillTreecomponent extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -5620172809015835852L;
    private List<Ability> abilities;
    private TreeMap<Long, String> abilityLog;
    private final static Logger LOGGER = Logger.getLogger( SkillTreecomponent.class.getName() );

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );

        if (abilities != null && !abilities.isEmpty()) {
            final Map<String, Point> rows = new HashMap<String, Point>();
            int x = 20;
            int y = 20;
            for (final Ability a : abilities) {
                rows.put( a.getName(), new Point( x, y ) );
                try {
                    final BufferedImage image = ImageCache.getAbilityImage( a.getName() );
                    if (image == null) {
                        g.drawString( a.getName(), x, y );
                        x += a.getName().length() * 6 + 5;
                    }
                    else {
                        g.drawImage( image, x, y, 64, 64, this );
                        x += 64 + 5;
                    }
                }

                catch (final IOException e) {
                    LOGGER.warning( "Error loading image: " + e.getLocalizedMessage() );
                    g.drawString( a.getName(), x, y );
                    x += a.getName().length() * 6 + 5;
                }
            }

            y = 20 + 64 + 15;
            int i = 1;
            for (final String s : abilityLog.values()) {
                x = (int) rows.get( s ).getX() + 32;
                g.setColor( Color.red );
                g.fillRect( x - 10, y - 10, 21, 21 );
                g.setColor( Color.white );
                g.drawString( i + "", x - 3, y + 3 );
                y += 22;
                i++;
            }
        }
    }

    public void setAbilities( List<Ability> abilities ) {
        if (abilities != this.abilities) {
            this.abilities = abilities;

            abilityLog = new TreeMap<Long, String>();
            for (final Ability a : abilities) {
                for (final Long l : a.getLevel().keySet()) {
                    if (a.getLevel().get( l ) > 0) {
                        abilityLog.put( l, a.getName() );
                    }
                }
            }
        }
    }

}
