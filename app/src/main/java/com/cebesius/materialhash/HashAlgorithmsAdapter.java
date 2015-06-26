package com.cebesius.materialhash;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.cebesius.materialhash.domain.HashAlgorithm;

import java.util.List;

public class HashAlgorithmsAdapter extends ArrayAdapter<HashAlgorithm> {

    public HashAlgorithmsAdapter(Context context, int resource, List<HashAlgorithm> objects) {
        super(context, resource, objects);
    }

}
