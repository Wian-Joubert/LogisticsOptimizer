package Controller;

import Model.Place;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class DistanceMatrixController {
    private static final Logger logger = LoggerFactory.getLogger(DistanceMatrixController.class);
    final FileController fileController = new FileController();

    public DistanceMatrix distanceMatrixCall(ArrayList<Place> places) {
        try {
            String API_KEY = fileController.readApiKey();
            if (API_KEY == null) {
                throw new RuntimeException("No API Key found.");
            }

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();

            ArrayList<String> origins = new ArrayList<>();
            ArrayList<String> destinations = new ArrayList<>();
            for (Place place : places) {
                StringBuilder location = new StringBuilder();
                location.append(place.getStreet()).append(", ");
                location.append(place.getTown()).append(", ");
                location.append(place.getCity()).append(", ");
                location.append(place.getPostcode()).append(", ");
                origins.add(location.toString());
                destinations.add(location.toString());
            }

            LatLng[] originsLatLngs = new LatLng[origins.size()];
            for (int i = 0; i < origins.size(); i++) {
                logger.info("Geocoding origin: {}", origins.toArray()[i]);
                GeocodingResult[] originsGeo = GeocodingApi.geocode(context, origins.toArray()[i].toString()).await();

                if (originsGeo.length == 0) {
                    throw new RuntimeException(String.format("Failed to Geocode Origin Address: %s", origins.toArray()[i].toString()));
                }

                originsLatLngs[i] = new LatLng(originsGeo[0].geometry.location.lat, originsGeo[0].geometry.location.lng);
                logger.info("Successfully Geocoded Origin {}: Latitude = {}, Longitude = {}", i, originsLatLngs[i].lat, originsLatLngs[i].lng);
            }

            LatLng[] destinationLatLngs = new LatLng[destinations.size()];
            for (int i = 0; i < destinations.size(); i++) {
                logger.info("Geocoding destination: {}", destinations.toArray()[i]);
                GeocodingResult[] destinationsGeo = GeocodingApi.geocode(context, destinations.toArray()[i].toString()).await();

                if (destinationsGeo.length == 0) {
                    throw new RuntimeException(String.format("Failed to Geocode Origin Address: %s.", destinations.toArray()[i].toString()));
                }

                destinationLatLngs[i] = new LatLng(destinationsGeo[0].geometry.location.lat, destinationsGeo[0].geometry.location.lng);
                logger.info("Successfully Geocoded Destination {}: Latitude = {}, Longitude = {}", i, destinationLatLngs[i].lat, destinationLatLngs[i].lng);
            }

            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                    .origins(originsLatLngs)
                    .destinations(destinationLatLngs)
                    .await();

            if (distanceMatrix.rows.length == 0) {
                throw new RuntimeException("Failed to retrieve Distance Matrix.");
            }

            return distanceMatrix;

        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Stack Trace:", ex);
            return null;
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Interrupt Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Stack Trace:", ex);
            return null;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Stack Trace:", ex);
            return null;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Distance Retrieval Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Stack Trace:", ex);
            return null;
        }
    }
}
