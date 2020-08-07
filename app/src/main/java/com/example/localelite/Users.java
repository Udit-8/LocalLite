package com.example.localelite;

import java.util.HashMap;
import java.util.Map;

public class Users {
    public String email,name,phone,type;

    public Users() {
    }

    public Users(String email, String name, String phone,String type) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.type = type;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("phone", phone);

        return result;
    }
}
