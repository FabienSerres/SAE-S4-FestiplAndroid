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
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
                int idFestival = Utils.getFestivalId(position);
                if (tag.equals("empty")) {
                    button.setImageResource(R.drawable.star_full);
                    button.setTag("full");

                    String addFavoriteUrl = String.format(Utils.URL_API_ADD_FAV, Utils.idUser,
                                                                                 idFestival);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(CallAPI.requestAddFavoriteFestival(addFavoriteUrl));
                } else if (tag.equals("full")) {
                    button.setImageResource(R.drawable.star_empty);
                    button.setTag("empty");

                    String deleteFavoriteUrl = String.format(Utils.URL_API_DEL_FAV, Utils.idUser,
                                                                                    idFestival);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(requestDeleteFavoriteFestival(deleteFavoriteUrl));
                }
            }
        });

        return listItemView;
    }

    

    /**
     * Creates a JsonObjectRequest for deleting a festival from favorites.
     *
     * @param url The URL for deleting the festival from favorites.
     * @return The JsonObjectRequest for deleting the festival from favorites.
     */
    private JsonObjectRequest requestDeleteFavoriteFestival(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), R.string.msg_success_delete_fav,
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ServerError && error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 500 || statusCode == 400) {
                                Toast.makeText(getContext(),
                                        R.string.msg_error_delete_fav,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put(Utils.API_KEY_NAME, Utils.apiKeyUser);
                return headers;
            }
        };
        return jsonObjectRequest;
    }
}

