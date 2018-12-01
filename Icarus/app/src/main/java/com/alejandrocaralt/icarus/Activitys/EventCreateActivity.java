package com.alejandrocaralt.icarus.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alejandrocaralt.icarus.Models.Event;
import com.alejandrocaralt.icarus.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.geojson.Point;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.mapbox.mapboxsdk.geometry.LatLng;
import android.support.annotation.NonNull;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class EventCreateActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMapClickListener {

    private EditText inputTitle;
    private Button createBtn;
    private Double routeKm;
    private Integer routeMin;
    private String routeName;

    private FirebaseFirestore db;
    private FirebaseAuth fbAuth;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private Location originLocation;
    private Marker originMarker;
    private Marker destinationMarker;
    private List<Marker> routeMarkers;
    private LatLng originCoord;
    private LatLng destinationCoord;
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "EventCreateActivity";
    private NavigationMapRoute navigationMapRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mb_api_key));
        setContentView(R.layout.activity_event_create);
        mapView = findViewById(R.id.ecreate_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        db = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();

        inputTitle = findViewById(R.id.ecreate_inputTitle);
        createBtn = findViewById(R.id.ecreate_inputBtn);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = inputTitle.getText().toString().trim() ;

                if (title.isEmpty() || navigationMapRoute == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.ecreate_empty), Toast.LENGTH_SHORT).show() ;
                } else {
                    addFirestoreEvent();
                }
            }
        });
    };

    private void addFirestoreEvent() {

        String eventTitle = inputTitle.getText().toString().trim();
        String userUid = fbAuth.getCurrentUser().getUid();

        List<String> eventBikers= new ArrayList<String>();
        eventBikers.add(userUid);

        List<Double> eventOriginPoint = new ArrayList<>();
        eventOriginPoint.add(originCoord.getLatitude());
        eventOriginPoint.add(originCoord.getLongitude());

        List<Double> eventDestinationPoin = new ArrayList<>();
        eventDestinationPoin.add(destinationCoord.getLatitude());
        eventDestinationPoin.add(destinationCoord.getLongitude());

        Event event = new Event(routeName, eventTitle, routeKm, routeMin, eventOriginPoint, eventDestinationPoin, userUid, eventBikers);



        db.collection(getString(R.string.fbDb_events_collection))
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), getString(R.string.ecreate_success), Toast.LENGTH_SHORT).show() ;

                        setResult(200);

                        Intent intent = new Intent(EventCreateActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.ecreate_error), Toast.LENGTH_SHORT).show() ;
                    }
                });
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        enableLocationComponent();
        routeMarkers = new ArrayList<>();
        originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
        mapboxMap.addOnMapClickListener(this);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
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


                        currentRoute = response.body().routes().get(0);
                        Integer i = (int)(currentRoute.distance() / 100);
                        Double e = (double)i;
                        routeKm = e / 10;
                        routeMin = (int)Math.round(currentRoute.duration() / 60);
                        routeName = currentRoute.legs().get(0).summary();


                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onMapClick(@NonNull LatLng point){

        if (routeMarkers.size() >= 2) {
            mapboxMap.clear();
            routeMarkers.clear();
            navigationMapRoute.removeRoute();

            originCoord = point;
            originMarker = mapboxMap.addMarker(new MarkerOptions().position(originCoord).title("Start"));
            routeMarkers.add(originMarker);
        } else if (routeMarkers.size() == 1) {
            destinationCoord = point;
            destinationMarker = mapboxMap.addMarker(new MarkerOptions().position(destinationCoord).title("End"));
            routeMarkers.add(destinationMarker);

            originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
            destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());

            getRoute(originPosition, destinationPosition);
        } else {
            originCoord = point;
            originMarker = mapboxMap.addMarker(new MarkerOptions().position(originCoord).title("Start"));
            routeMarkers.add(originMarker);
        }

    }
}
