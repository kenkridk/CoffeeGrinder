package com.example.kenneth.coffeegrinder;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CoffeeReadyFragment extends Fragment {

    public CoffeeReadyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coffee_ready, container, false);
    }

    public void setText(String text) {
        TextView textView = (TextView) getView().findViewById(R.id.coffeeReadyText);
        textView.setText(text);
    }
}
