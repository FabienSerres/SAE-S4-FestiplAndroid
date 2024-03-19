package fr.iut.festiplandroid.utils;

import android.content.Context;
import android.content.Intent;
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

import fr.iut.festiplandroid.DetailsFestivalActivity;
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

        ImageButton buttonFav = listItemView.findViewById(R.id.btn_view);
        buttonFav.setTag("empty");

        ImageButton buttonDetails = listItemView.findViewById(R.id.btn_details);

        String[] festivalInfo = ListFestivalActivity.allFestivals.get(position + 1);

        if (festivalInfo != null && festivalInfo.length > 1 && festivalInfo[1].equals("true") ||
            ListFestivalActivity.favoritesFestivalList.contains(currentItem)) {
            buttonFav.setImageResource(R.drawable.star_full);
            buttonFav.setTag("full");
        } else {
            buttonFav.setImageResource(R.drawable.star_empty);
        }

        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) buttonFav.getTag();
                int idFestival = Utils.getFestivalId(position);
                if (tag.equals("empty")) {
                    buttonFav.setImageResource(R.drawable.star_full);
                    buttonFav.setTag("full");

                    String addFavoriteUrl = String.format(Utils.URL_API_ADD_FAV, Utils.idUser,
                                                                                 idFestival);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(requestAddFavoriteFestival(addFavoriteUrl));
                } else if (tag.equals("full")) {
                    buttonFav.setImageResource(R.drawable.star_empty);
                    buttonFav.setTag("empty");

                    String deleteFavoriteUrl = String.format(Utils.URL_API_DEL_FAV, Utils.idUser,
                                                                                    idFestival);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(requestDeleteFavoriteFestival(deleteFavoriteUrl));
                }
            }
        });

        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameFestival = getItem(position);
                int id = 0;
                for (Map.Entry<Integer, String[]> values : ListFestivalActivity.allFestivals.entrySet()) {
                    if (values.getValue()[0].equals(nameFestival)) {
                        id = values.getKey();
                    }
                }

                Intent intent = new Intent(getContext(), DetailsFestivalActivity.class);
                intent.putExtra("idFestival", id);
                getContext().startActivity(intent);
            }
        });

        return listItemView;
    }

    /**
     * Creates a JsonObjectRequest for adding a festival to favorites.
     *
     * @param url The URL for adding the festival to favorites.
     * @return The JsonObjectRequest for adding the festival to favorites.
     */
    private JsonObjectRequest requestAddFavoriteFestival(String url) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getContext(), R.string.msg_success_add_fav,
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
                                    R.string.msg_error_add_fav,
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

