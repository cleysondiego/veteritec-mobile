package br.com.veteritec.customers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetCustomersResponseStructure {
    private List<Customer> customersList = new ArrayList<>();

    public List<Customer> getCustomersList() {
        return customersList;
    }

    public GetCustomersResponseStructure fromJson(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("customers");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                customersList.add(new Customer().fromJson(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Customer implements Serializable {
        private String id;
        private String cpf;
        private String name;
        private String zipCode;
        private String street;
        private String number;
        private String neighborhood;
        private String phoneNumber;
        private String cellPhoneNumber;
        private String email;
        private String clinicId;

        public String getId() {
            return id;
        }

        public String getCpf() {
            return cpf;
        }

        public String getName() {
            return name;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getStreet() {
            return street;
        }

        public String getNumber() {
            return number;
        }

        public String getNeighborhood() {
            return neighborhood;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getCellPhoneNumber() {
            return cellPhoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public String getClinicId() {
            return clinicId;
        }

        public Customer fromJson(JSONObject jsonObject) {
            try {
                this.id = jsonObject.getString("_id");
                this.cpf = jsonObject.getString("cpf");
                this.name = jsonObject.getString("name");
                this.zipCode = jsonObject.getString("zipcode");
                this.street = jsonObject.getString("street");
                this.number = jsonObject.getString("number");
                this.neighborhood = jsonObject.getString("neighborhood");
                this.phoneNumber = jsonObject.getString("phoneNumber");
                this.cellPhoneNumber = jsonObject.getString("cellNumber");
                this.email = jsonObject.getString("email");
                this.clinicId = jsonObject.getString("clinic");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
