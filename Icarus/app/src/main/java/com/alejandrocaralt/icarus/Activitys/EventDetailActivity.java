package com.alejandrocaralt.icarus.Activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alejandrocaralt.icarus.Adapters.EventAdapter;
import com.alejandrocaralt.icarus.Models.Event;
import com.alejandrocaralt.icarus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import java.util.List;


public class EventDetailActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMapClickListener {
    private Event event;

    private TextView txtTitle, txtRouteName;
    private Button inputBtn;

    private FirebaseAuth fbAuth;
    private FirebaseFirestore db;

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker originMarker;
    private Marker destinationMarker;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "EventDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         // IMPORTANT, THIS LINE MUST BE ON THE TOP!!!!!
        Mapbox.getInstance(this,getString(R.string.mb_api_key));

        setContentView(R.layout.activity_event_detail);

        // MapBox code
        mapView = (MapView) findViewById(R.id.edetail_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        final Intent intent = getIntent();
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtTitle = findViewById(R.id.det_labelTitle);
        txtRouteName = findViewById(R.id.det_labelRoute);
        inputBtn = findViewById(R.id.det_inputBtn);

        event = (Event) intent.getSerializableExtra(getString(R.string.serializable_event));

        txtTitle.setText(event.getTitle());
        txtRouteName.setText(event.getRouteName());


        for (String s : event.getBikers()) {
            String u = fbAuth.getCurrentUser().getUid();
            if (s.equals(u)) {
                inputBtn.setEnabled(false);
                inputBtn.setBackgroundResource(R.color.btnDisabled);
            } else {
                inputBtn.setEnabled(true);
            }
        }



        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = db.collection(getString(R.string.fbDb_events_collection)).document(event.getId());
                String usr = fbAuth.getCurrentUser().getUid();

                docRef.update(getString(R.string.fbDb_bikers_document), FieldValue.arrayUnion(usr)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            setResult(200) ;
                            Toast.makeText(EventDetailActivity.this, getString(R.string.edetail_participating_successful), Toast.LENGTH_SHORT).show() ;
                        } else {
                            Toast.makeText(EventDetailActivity.this, getString(R.string.edetail_participating_error), Toast.LENGTH_SHORT).show() ;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventDetailActivity.this, getString(R.string.edetail_participating_error), Toast.LENGTH_SHORT).show() ;
                    }
                });
            }
        });

    }


    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }

        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        // Generate rout to click point from location
        // map.addOnMapClickListener(this);

        LatLng o = new LatLng(event.getOrigin().get(0), event.getOrigin().get(1));
        LatLng d = new LatLng(event.getDestination().get(0), event.getDestination().get(1));

        destinationPosition = Point.fromLngLat(d.getLongitude(), d.getLatitude());
        originPosition = Point.fromLngLat(o.getLongitude(), o.getLatitude());

        getRoute(originPosition, destinationPosition);
        enableLocation();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocation() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            LocationComponent locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(this);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            originLocation = locationComponent.getLastKnownLocation();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }
    @SuppressLint("WrongConstant")
    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPostition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng((location.getLatitude()),
                location.getLongitude()),
                13.0)
        );
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        DirectionsRoute currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPostition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (destinationMarker != null) {
            map.removeMarker(destinationMarker);
        }
        destinationMarker = map.addMarker(new MarkerOptions().position(point));

        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

        getRoute(originPosition, destinationPosition);
    }
}
