package fr.iut.festiplandroid.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.iut.festiplandroid.ListFestivalActivity;
import fr.iut.festiplandroid.R;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_layout, parent, false);
        }

        String currentItem = getItem(position);

        TextView textView = listItemView.findViewById(R.id.text_view);
        textView.setText(currentItem);

        ImageButton button = listItemView.findViewById(R.id.btn_view);
        button.setTag("empty");

        String[] festivalInfo = ListFestivalActivity.allFestivals.get(position + 1);

        if (festivalInfo != null && festivalInfo.length > 1 && festivalInfo[1].equals("true") ||
            ListFestivalActivity.favoritesFestivalList.contains(currentItem)) {
            button.setImageResource(R.drawable.star_full);
            button.setTag("full");
        } else {
            button.setImageResource(R.drawable.star_empty);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) button.getTag();
                if (tag.equals("empty")) {
                    button.setImageResource(R.drawable.star_full);
                    button.setTag("full");
                } else if (tag.equals("full")) {
                    button.setImageResource(R.drawable.star_empty);
                    button.setTag("empty");
                }
            }
        });

        return listItemView;
    }

}

