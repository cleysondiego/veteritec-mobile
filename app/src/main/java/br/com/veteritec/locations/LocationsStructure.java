package br.com.veteritec.locations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationsStructure {
    private List<Location> locationList = new ArrayList<>();

    public List<Location> getLocationList() {
        return locationList;
    }

    public LocationsStructure fromJson(JSONObject jsonObject) {
        try {
            JSONObject object = jsonObject.getJSONObject("location");

            locationList.add(new Location().fromJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Location implements Serializable {
        private String latitude;
        private String longitude;
        private String createdAt;

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public Location fromJson(JSONObject jsonObject) {
            try {
                this.latitude = jsonObject.getString("latitude");
                this.longitude = jsonObject.getString("longitude");
                this.createdAt = jsonObject.getString("createdAt");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
