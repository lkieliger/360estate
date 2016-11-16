package ch.epfl.sweng.project.user;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.DataMgmt;


@ParseClassName("Favorites")
public class Favorites extends ParseObject {

    private static final String TAG = "Favorites";
    private Boolean hasLocalDataChanged = false;
    private Set<String> favorites = new HashSet<>();

    public Favorites() {

    }

    public Favorites(HashSet<String> extFavorites, String idUser) {
        setFavorites(extFavorites);
        setIdUser(idUser);
    }

    public void setIdUser(String idUser) {
        put("idUser", idUser);
    }

    public Boolean getHasLocalDataChanged() {
        return hasLocalDataChanged;
    }

    public void setFavorites(Set<String> favorites) {
        JSONArray jsonFavorites = new JSONArray(favorites);
        put("favorites", jsonFavorites);
        try {
            save();
        } catch (ParseException e) {
            Log.d(TAG,e.getMessage());
        }
    }

    public String getIdUser() {
        return getString("idUser");
    }

    public Set<String> getFavoritesFromLocal() {
        return favorites;
    }

    public boolean containsUrl(String url){
        return favorites.contains(url);
    }

    public void addUrlToLocal(String newUrl){
        favorites.add(newUrl);
        hasLocalDataChanged = true;
    }

    public void deleteUrlToLocal(String url){
        favorites.remove(url);
        hasLocalDataChanged = true;
    }

    public Set<String> getFavoritesFromServer() {
        JSONArray urlArray = getJSONArray("favorites");
        Set<String> favoritesSet = new HashSet<>();

        if (urlArray == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error parsing the favoritesList array from JSON");
            }
            return favoritesSet;
        }

        for (int i = 0; i < urlArray.length(); i++) {
            try {
                favoritesSet.add(urlArray.getString(i));
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        return favoritesSet;
    }

    public void synchronizeFromServer() {
        favorites = getFavoritesFromServer();
        hasLocalDataChanged = true;
    }

    public void synchronizeServer() {
        DataMgmt.overrideFavorites(getIdUser(),favorites);
        hasLocalDataChanged = false;
    }
}
