package br.com.veteritec.clinics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClinicResponseStructure implements Serializable {
    private List<Clinic> clinics = new ArrayList<>();

    public List<Clinic> getClinics() {
        return clinics;
    }

    public ClinicResponseStructure fromJson(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("clinics");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                clinics.add(new Clinic().fromJson(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Clinic implements Serializable {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Clinic fromJson(JSONObject jsonObject) {
            try {
                this.id = jsonObject.getString("_id");
                this.name = jsonObject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
