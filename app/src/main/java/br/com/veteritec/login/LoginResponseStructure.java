package br.com.veteritec.login;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponseStructure {
    private UserConfig userConfig;

    private String token;

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public String getToken() {
        return token;
    }

    public LoginResponseStructure fromJson(JSONObject jsonObject) {
        try {
            this.userConfig = new UserConfig().fromJson(jsonObject.getJSONObject("user"));
            this.token = jsonObject.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class UserConfig {
        private String name;

        public String getName() {
            return name;
        }

        public UserConfig fromJson(JSONObject jsonObject) {
            try {
                this.name = jsonObject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
