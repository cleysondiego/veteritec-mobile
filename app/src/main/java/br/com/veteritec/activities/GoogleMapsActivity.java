package br.com.veteritec.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.veteritec.R;
import br.com.veteritec.locations.GetLocationsUseCase;
import br.com.veteritec.locations.LocationsStructure;
import br.com.veteritec.usecase.ThreadExecutor;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private String PET_ID = "5efe670d61b7c60025478f5d";
    private GoogleMap mMap;
    private Context context;

    private String userToken = "";
    private String userClinicId = "";

    private LocationsStructure locationsStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        context = getApplicationContext();

        locationsStructure = new LocationsStructure();
        getUserDataFromSharedPreferences(context);
        getLocations();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setInfos(locationsStructure.getLocationList());
    }

    private void getUserDataFromSharedPreferences(Context context) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
        userToken = sharedPreferencesUtils.getUserToken(context);
        userClinicId = sharedPreferencesUtils.getUserClinicId(context);
    }

    private void getLocations() {
        ApiRequest apiRequest = new ApiRequest();

        GetLocationsUseCase getLocationsUseCase = new GetLocationsUseCase(ThreadExecutor.getInstance(), apiRequest, userClinicId, userToken, PET_ID);
        getLocationsUseCase.setCallback(new GetLocationsUseCase.OnGetLocationsCallback() {
            @Override
            public void onSuccess(LocationsStructure getLocationsStructure) {
                locationsStructure = getLocationsStructure;
                setInfos(locationsStructure.getLocationList());
            }

            @Override
            public void onFailure(int statusCode) {
                Toast.makeText(context, "O Pet selecionado não possui acesso a essa funcionalidade!", Toast.LENGTH_LONG).show();
            }
        });

        getLocationsUseCase.execute();
    }

    private void setInfos(List<LocationsStructure.Location> locations) {
        double latitude = -23.0882;
        double longitude = -47.2215;
        String createdAt;
        String lastUpdated = "";
        for (LocationsStructure.Location location : locations) {
            latitude = Double.parseDouble(location.getLatitude());
            longitude = Double.parseDouble(location.getLongitude());
            createdAt = location.getCreatedAt();
            String[] time = createdAt.split("\\.");
            String lastTime = time[0].replace("T", " ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = new Date();
            try {
                parsedDate = dateFormat.parse(lastTime);
            } catch (ParseException ignored) {}

            if (parsedDate != null) {
                Timestamp timestamp = new Timestamp(parsedDate.getTime());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                lastUpdated = simpleDateFormat.format(timestamp.getTime() - 10800000);
            }
        }

        LatLng petLocalization = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(petLocalization).title("Localização do pet. Atualizado em: " + lastUpdated));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(petLocalization, 15), 3000, null);
    }
}