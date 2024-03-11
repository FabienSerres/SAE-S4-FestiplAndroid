package fr.iut.festiplandroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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

        // Récupérez l'élément actuel dans la liste
        String currentItem = getItem(position);

        // Trouvez la vue texte et définissez le texte approprié
        TextView textView = listItemView.findViewById(R.id.text_view);
        textView.setText(currentItem);

        // Trouvez le bouton et définissez son action
        ImageButton button = listItemView.findViewById(R.id.btn_view);

        String[] festivalInfo = ListFestivalActivity.allFestivals.get(position);
        if (festivalInfo != null && festivalInfo.length > 1 && festivalInfo[1].equals("true")) {
            // Si le favori est true, définir l'image du bouton sur l'icône de favori plein
            button.setImageResource(R.drawable.star_full);
        } else {
            // Sinon, définir l'image du bouton sur l'icône de favori vide
            button.setImageResource(R.drawable.star_empty);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à effectuer lorsque le bouton est cliqué
                Toast.makeText(getContext(), "Bouton cliqué pour : " + currentItem, Toast.LENGTH_SHORT).show();
            }
        });

        return listItemView;
    }
}

