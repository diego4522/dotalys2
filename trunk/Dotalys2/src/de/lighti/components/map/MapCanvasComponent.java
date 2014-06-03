package de.lighti.components.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MapCanvasComponent extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 2077175805479363567L;
    private static final int DEFAULT_DOT_SIZE = 3;
    private BufferedImage minimap;
    private BufferedImage minimapModel;
    private int[] markers = new int[0];
    private int dotSize = DEFAULT_DOT_SIZE;
    private final String MINIMAP_FILE = "Minimap.jpg";
    private final String MINIMAP_MODEL_FILE = "Mapmodel.png";

    private boolean paintMapModel;

    public MapCanvasComponent() {
        final Dimension size = new Dimension( 512, 512 );
        setMinimumSize( size );
        setMaximumSize( size );
        setPreferredSize( size );

        paintMapModel = false;

        try {
            URL url = MapCanvasComponent.class.getResource( MINIMAP_FILE );
            minimap = ImageIO.read( url );
            url = MapCanvasComponent.class.getResource( MINIMAP_MODEL_FILE );
            minimapModel = ImageIO.read( url );
        }
        catch (final IOException e) {
            JOptionPane.showMessageDialog( this, e.getLocalizedMessage(), "Error loading minimap", JOptionPane.ERROR_MESSAGE );
        }
    }

    public int getDotSize() {
        return dotSize;
    }

    public boolean isPaintMapModel() {
        return paintMapModel;
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if (paintMapModel) {
            g.drawImage( minimapModel, 0, 0, getWidth(), getHeight(), null );
        }
        else {
            g.drawImage( minimap, 0, 0, getWidth(), getHeight(), null );
        }
        final double xScale = getWidth() / 128.0;
        final double yScale = getHeight() / 128.0;
        for (int offset = 0; offset < markers.length; offset += 2) {

            g.setColor( Color.red );
            int x = markers[offset];
            x *= xScale;
            // x = getWidth() - x;
            x -= 256;
            int y = markers[offset + 1];
            y *= yScale;
            y = getWidth() - y;
            y += 256;
            // System.out.println( getWidth() + ", " + getHeight() );
            // System.out.println( xScale + ", " + yScale );
            // System.out.println( x + "," + y );
            g.fillRect( x - dotSize / 2, y - dotSize / 2, dotSize, dotSize );
        }
    }

    public void resetDotSize() {
        dotSize = DEFAULT_DOT_SIZE;
    }

    public void setDotSize( int dotSize ) {
        this.dotSize = dotSize;
    }

    public void setMarkers( int[] markers ) {
        this.markers = markers;
        this.repaint();
    }

    public void setPaintMapModel( boolean paintMapModel ) {
        this.paintMapModel = paintMapModel;
    }

}
