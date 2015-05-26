package dk.au.teamawesome.promulgate.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.au.teamawesome.promulgate.R;


public class ShowMapTextFragment extends Fragment {

    public ShowMapTextFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_map_text, container, false);
    }

    public void setText(String text) {
        TextView textView = (TextView) getView().findViewById(R.id.showMapText);
        textView.setText(text);
    }
}
