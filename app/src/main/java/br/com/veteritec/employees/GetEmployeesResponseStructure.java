package br.com.veteritec.employees;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetEmployeesResponseStructure {
    private List<Employee> employeeList = new ArrayList<>();

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public GetEmployeesResponseStructure fromJson(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("users");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                employeeList.add(new Employee().fromJson(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Employee implements Serializable {
        private String id;
        private String name;
        private String email;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public Employee fromJson(JSONObject jsonObject) {
            try {
                this.id = jsonObject.getString("_id");
                this.name = jsonObject.getString("name");
                this.email = jsonObject.getString("email");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
