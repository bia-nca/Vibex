package de.girlsgeek.vibex;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class SimpleXmlPull {

    public static JSONArray getJSONArrayFromXML(String xmlString) throws XmlPullParserException{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        JSONArray parsedEvents = new JSONArray();
        JSONObject currentEvent = new JSONObject();
        JSONArray currentPerformer = new JSONArray();
        boolean parsingDone = false;
        int count = 0; // current event iteration

        try {
            parser.setInput( new StringReader(xmlString) );
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT && !parsingDone) {
                String tagName = null;

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG: // von hier bis zum nächsten comment ist noch Arbeit da... alles in eine Liste speichern und so
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("event")) {
                            // main tag "event"... so clear the current Event
                            currentEvent = new JSONObject();
                            currentPerformer = new JSONArray();
                        } else if (tagName.equalsIgnoreCase("latitude")) {
                            currentEvent.put("lat", parser.getText());
                        } else if (tagName.equalsIgnoreCase("longitude")) {
                            currentEvent.put("lng", parser.getText());
                        } else if (tagName.equalsIgnoreCase("venue_address")) {
                            currentEvent.put("venue_adress", parser.getText());
                        } else if (tagName.equalsIgnoreCase("venue_name")) {
                            currentEvent.put("venue_name", parser.getText());
                        } else if (tagName.equalsIgnoreCase("venue_url")) {
                            currentEvent.put("venue_url", parser.getText());
                        } else if (tagName.equalsIgnoreCase("name")) { // performer name
                            currentPerformer.put(parser.getText());
                        }
                        break;

                    case XmlPullParser.END_TAG: // ab hier sollte schon alles gut sein
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("event")) {
                            currentEvent.put("performer", currentPerformer); // add performer array to event json
                            parsedEvents.put(count, currentEvent); // add all events to main JSON-Array
                            count++; // increment to continue with the next event
                        } else if (tagName.equalsIgnoreCase("event")) {
                            parsingDone = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
            // End of document

            return parsedEvents;

        } catch (Exception e) {
            Log.e("Event List", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}

