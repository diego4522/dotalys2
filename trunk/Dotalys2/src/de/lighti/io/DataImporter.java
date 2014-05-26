package de.lighti.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.lighti.DotaPlay;
import de.lighti.DotaPlay.ProgressListener;
import de.lighti.model.AppState;
import de.lighti.parsing.AbilityTracker;
import de.lighti.parsing.CreepHandler;
import de.lighti.parsing.HeroTracker;
import de.lighti.parsing.ItemListener;
import de.lighti.parsing.PlayersListener;

public final class DataImporter {
    public static final FileFilter FILE_FILTER = new FileFilter() {

        @Override
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( ".dem" );
        }

        @Override
        public String getDescription() {
            return "Dota2 Replay files (*.dem)";
        }
    };

    public static void parseReplayFile( AppState appState, File file, ProgressListener... listeners ) {
        DotaPlay.getListeners().clear();
        DotaPlay.addListener( new PlayersListener( appState ) );
        DotaPlay.addListener( new ItemListener( appState ) );
        DotaPlay.addListener( new AbilityTracker( appState ) );
        DotaPlay.addListener( new HeroTracker( appState ) );
        DotaPlay.addListener( new CreepHandler( appState ) );
        DotaPlay.loadFile( file.getAbsolutePath() );
    }

    public static void readLocalisedHeroNames( URL resource, AppState appState ) {
        try {
            //Get the DOM Builder Factory
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            //Get the DOM Builder
            final DocumentBuilder builder = factory.newDocumentBuilder();

            //Load and Parse the XML document
            //document contains the complete XML as a Tree.
            final Document document = builder.parse( resource.getFile() );

            //Iterating through the nodes and extracting the data.
            final NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                final Node node = nodeList.item( i );
                if (node instanceof Element) {
                    if (!node.getNodeName().equals( "heroes" )) {
                        continue;
                    }

                    final NodeList childNodes = node.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        final Node cNode = childNodes.item( j );
                        if (!(cNode instanceof Element) || !cNode.getNodeName().equals( "hero" )) {
                            continue;
                        }
                        String name = null;
                        String localisedName = null;

                        final NodeList childChildNodes = cNode.getChildNodes();
                        for (int h = 0; h < childChildNodes.getLength(); h++) {
                            final Node ccNode = childChildNodes.item( h );
                            if (!(ccNode instanceof Element)) {
                                continue;
                            }
                            final String content = ccNode.getLastChild().getTextContent().trim();
                            if (ccNode.getNodeName().equals( "name" )) {
                                name = content;
                            }
                            else if (ccNode.getNodeName().equals( "localized_name" )) {
                                localisedName = content;
                            }
                        }
                        if (name == null || localisedName == null) {
                            throw new SAXException( "Found hero without name or localised name" );
                        }
                        else {
                            appState.setHeroName( name, localisedName );
                        }

                    }

                }

            }
        }
        catch (final IOException | ParserConfigurationException | SAXException e) {
            throw new IllegalStateException( e );
        }
    }

    private DataImporter() {

    }
}
