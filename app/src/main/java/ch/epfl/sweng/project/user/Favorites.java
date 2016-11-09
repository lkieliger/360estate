package ch.epfl.sweng.project.user;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;


@ParseClassName("Favorites")
public class Favorites extends ParseObject {

    private static final String TAG = "Favorites";

    private List<String> favorites = null;

    public Favorites(){

    }

    public Favorites(List<String> extFavorites, int idUser){
        favorites = extFavorites;
        setFavorites(favorites);
        setIdUser(idUser);
    }

    public void setIdUser(int idUser) {
        put("idUser", idUser);
    }

    public void setFavorites(List<String> favorites) {
        JSONArray jsonFavorites = new JSONArray(favorites);
        put("favorites", jsonFavorites);
    }

    String getIdUser() {
        return getString("idUser");
    }

    List<String> getFavorites() {
        JSONArray urlArray = getJSONArray("favorites");
        List<String> favoritesList = new ArrayList<>();

        if (urlArray == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error parsing the favoritesList array from JSON");
            }
            return favoritesList;
        }

        for (int i = 0; i < urlArray.length(); i++) {
            try {
                favoritesList.add(urlArray.getString(i));
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        return favoritesList;
    }

    public void synchronizeFromServer(){
        favorites = getFavorites();
    }

    public void synchronizeServer(){
        setFavorites(favorites);
    }
}
