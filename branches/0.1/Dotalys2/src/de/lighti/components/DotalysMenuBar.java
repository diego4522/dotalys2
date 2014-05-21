package de.lighti.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.lighti.DotaPlay.ProgressListener;
import de.lighti.components.batch.BatchDialog;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.Dotalys2App;

public class DotalysMenuBar extends JMenuBar {
    private final Dotalys2App owner;

    private JMenu fileMenu;
    private JMenuItem fileOpenMenuItem;

    private JMenuItem batchExportMenuItem;

    public DotalysMenuBar( Dotalys2App o ) {
        super();

        owner = o;

        add( getFileMenu() );
        add( getBatchExportItem() );
    }

    private JMenuItem getBatchExportItem() {
        if (batchExportMenuItem == null) {
            batchExportMenuItem = new JMenuItem( "Batch Export" );
            batchExportMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent arg0 ) {
                    new BatchDialog( owner ).setVisible( true );

                }
            } );
        }

        return batchExportMenuItem;
    }

    public JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu( "File" );
            fileMenu.add( getFileOpenMenuItem() );
        }
        return fileMenu;
    }

    public JMenuItem getFileOpenMenuItem() {
        if (fileOpenMenuItem == null) {
            fileOpenMenuItem = new JMenuItem();
            fileOpenMenuItem.setAction( new AbstractAction() {

                @Override
                public void actionPerformed( ActionEvent e ) {

                    //Create a file chooser
                    final JFileChooser fc = new JFileChooser( "." );
                    fc.setFileFilter( DataImporter.FILE_FILTER );

                    //In response to a button click:
                    final int returnVal = fc.showOpenDialog( owner );
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        final AppState appState = owner.getAppState();
                        appState.clear();
                        final ProgressDialog pd = new ProgressDialog( owner );
                        final long fs = fc.getSelectedFile().length();
                        pd.setMaximum( fs );
                        final Thread t = new Thread( new Runnable() {

                            @Override
                            public void run() {

                                DataImporter.parseFile( appState, fc.getSelectedFile(), new ProgressListener() {

                                    @Override
                                    public void bytesRemaining( int position ) {
                                        pd.setValue( fs - position );

                                    }
                                } );
                                pd.setVisible( false );
                            }
                        } );
                        t.start();
                        pd.setVisible( true );

                    }
                }
            } );
            fileOpenMenuItem.setText( "Open" );
        }

        return fileOpenMenuItem;
    }
}
