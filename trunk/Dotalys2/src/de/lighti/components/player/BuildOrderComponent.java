package de.lighti.components.player;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Queue;

import javax.swing.JPanel;

import de.lighti.model.game.Dota2Item;

public class BuildOrderComponent extends JPanel {
    private Queue<Dota2Item> items;

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );

        int x = 20;
        int y = 20;
        if (items != null) {
            for (final Dota2Item i : items) {
                try {
                    final BufferedImage image = i.getImage();
                    if (image != null) {
                        g.drawImage( image, x, y, null );
                        x += image.getWidth() + 5;
                    }
                    else {
                        g.drawString( i.getName(), x, y );
                        x += i.getName().length() * 20;
                    }
                }
                catch (final IOException e) {
                    System.err.println( "Error loading image: " + e.getLocalizedMessage() );
                    g.drawString( i.getName(), x, y );
                    x += i.getName().length() * 20;
                }

                if (x >= (getWidth() - 100)) {
                    y += 100;
                    x = 20;
                }
            }
        }
    }

    public void setItems( Queue<Dota2Item> items ) {
        if (items != this.items) {
            this.items = items;
            repaint();
        }
    }

}
