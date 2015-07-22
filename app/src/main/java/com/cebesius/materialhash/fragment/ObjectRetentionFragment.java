package com.cebesius.materialhash.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class ObjectRetentionFragment extends Fragment {

    private Map<String, Object> objects = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public <T> T get(String key) {
        return (T) objects.get(key);
    }

    public void put(String key, Object object) {
        objects.put(key, object);
    }

    public boolean containsKey(String key) {
        return objects.containsKey(key);
    }
}
