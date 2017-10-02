package com.biz.navimate.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Siddharth on 02-10-2017.
 */

public class Route {
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ROUTE";

    // ----------------------- Classes ---------------------------//
    // Class to define a single step in a route
    public static class Step
    {
        // Start and end of this step
        public ArrayList<LatLng> points = null;

        // Constructor
        public Step(ArrayList<LatLng> points)
        {
            this.points = points;
        }
    }

    // Base class for different type of route objects
    // Provides APIs which any route related operation may need.
    // Inherited classes must implement the APIs as per the route structure
    public static abstract class Base
    {
        // Globals
        public Polyline polyline        = null;

        // Abstracts
        // Distance / Duration getter
        public abstract int GetDurationS();
        public abstract int GetDistanceM();

        // API to get alll points on this route
        public abstract ArrayList<LatLng> GetAllPoints();

        // checkpoint getter
        public abstract ArrayList<LatLng> GetCheckpoints();

        // Equals
        public abstract boolean equals(Object object);

        // Public APIs
        // API to provide polyline options to populate UI of route on map. Should be implemented by derived classes.
        public PolylineOptions GetPolylineOptions()
        {
            // Init polyline options
            PolylineOptions polyOptions = new PolylineOptions();

            // Add points on the route
            polyOptions.addAll(GetAllPoints());

            // Init width of polyline
            polyOptions.width(15);

            return polyOptions;
        }
    }

    // A single route is a collection of position on map in a sequential order
    // starting from a source and ending at a destination
    public static class Leg extends Base
    {
        // List of steps in this route
        public ArrayList<Step> steps    = null;

        // Distance / Duration of route
        private int durationS = 0;
        private int distanceM = 0;

        // Constructor
        public Leg(ArrayList<Step> steps, int durationS, int distanceM)
        {
            this.steps = steps;
            this.durationS = durationS;
            this.distanceM = distanceM;
        }

        // Overrides
        @Override
        public int GetDurationS()
        {
            return durationS;
        }

        @Override
        public int GetDistanceM()
        {
            return distanceM;
        }

        @Override
        public ArrayList<LatLng> GetAllPoints()
        {
            ArrayList<LatLng> points = new ArrayList<>();

            if ((steps != null) && (steps.size() > 0))
            {
                // Add all points from all steps
                for (Step step :steps)
                {
                    points.addAll(step.points);
                }
            }

            return points;
        }

        @Override
        public ArrayList<LatLng> GetCheckpoints()
        {
            ArrayList<LatLng> checkpoints = new ArrayList<>();

            if ((steps != null) && (steps.size() > 0))
            {
                // Add Start
                checkpoints.add(steps.get(0).points.get(0));

                // Add last point of last step
                Step lastStep = steps.get(steps.size() - 1);
                checkpoints.add(lastStep.points.get(lastStep.points.size() - 1));
            }

            return checkpoints;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean bEqual = false;

            // Validate Object Instance
            if ((object != null) && (object instanceof Leg))
            {
                // Cast object to compare
                Leg compareObject = (Leg) object;

                // Get Checkpoints
                ArrayList<LatLng> compareCheckpoints = compareObject.GetCheckpoints();
                ArrayList<LatLng> currentCheckpoints = GetCheckpoints();

                // Ensure there are equal and a valid number of checkpoints in both objects
                if ((compareCheckpoints.size() == currentCheckpoints.size()) && (compareCheckpoints.size() > 0))
                {
                    // Mark bEqual as true
                    bEqual = true;

                    // Compare each checkpoint
                    for (int i = 0; i < compareCheckpoints.size(); i++)
                    {
                        LatLng compareCheckpoint = compareCheckpoints.get(i);
                        LatLng currentCheckpoint = currentCheckpoints.get(i);

                        if ((compareCheckpoint.longitude != currentCheckpoint.longitude) || (compareCheckpoint.latitude != currentCheckpoint.latitude))
                        {
                            bEqual = false;
                            break;
                        }
                    }
                }
            }

            return bEqual;
        }
    }

    // A multi route is a collection of single routes.
    // A multi route has the ability to maintain waypoints. I.e. the number of checkpoints.
    // In a single route the checkpoints are limited to 2 (start and end)
    // Route route can contain more than 2 checkpoints
    public static class Way extends Base
    {
        // List of routes in this route
        public ArrayList<Leg> legs = null;

        // Constructor
        public Way(ArrayList<Leg> legs)
        {
            this.legs = legs;
        }

        // Overrides
        @Override
        public int GetDurationS()
        {
            // Add duration of all routes
            int durationS = 0;

            for (Leg leg : legs)
            {
                durationS += leg.durationS;
            }

            return durationS;
        }

        @Override
        public int GetDistanceM()
        {
            // Add distance of all routes
            int distanceM = 0;

            for (Leg leg : legs)
            {
                distanceM += leg.distanceM;
            }

            return distanceM;
        }

        @Override
        public ArrayList<LatLng> GetAllPoints()
        {
            ArrayList<LatLng> points = new ArrayList<>();

            // Add all points of all legs
            for (Leg leg : legs)
            {
                points.addAll(leg.GetAllPoints());
            }

            return points;
        }

        @Override
        public ArrayList<LatLng> GetCheckpoints()
        {
            ArrayList<LatLng> checkpoints = new ArrayList<>();

            if ((legs != null) && (legs.size() > 0))
            {
                // Add start of first leg
                checkpoints.add(legs.get(0).GetCheckpoints().get(0));

                // Add end of all legs
                for (Leg leg : legs)
                {
                    checkpoints.add(leg.GetCheckpoints().get(1));
                }
            }

            return checkpoints;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean bEqual = false;

            // Validate Object Instance
            if ((object != null) && (object instanceof Route))
            {
                // Cast object to compare
                Way compareObject = (Way) object;

                // Get Checkpoints
                ArrayList<LatLng> compareCheckpoints = compareObject.GetCheckpoints();
                ArrayList<LatLng> currentCheckpoints = GetCheckpoints();

                // Ensure there are equal and a valid number of checkpoints in both objects
                if ((compareCheckpoints.size() == currentCheckpoints.size()) && (compareCheckpoints.size() > 0))
                {
                    // Mark bEqual as true
                    bEqual = true;

                    // Compare each checkpoint
                    for (int i = 0; i < compareCheckpoints.size(); i++)
                    {
                        LatLng compareCheckpoint = compareCheckpoints.get(i);
                        LatLng currentCheckpoint = currentCheckpoints.get(i);

                        if ((compareCheckpoint.longitude != currentCheckpoint.longitude) || (compareCheckpoint.latitude != currentCheckpoint.latitude))
                        {
                            bEqual = false;
                            break;
                        }
                    }
                }
            }

            return bEqual;
        }
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
