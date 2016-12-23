package ch.epfl.sweng.project.data.parse.objects;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.util.LogHelper;


@ParseClassName("Favorites")
public class Favorites extends ParseObject {

    private static final String TAG = "Favorites";
    private final String TAGFav = "mFavoritesSet";
    private Set<String> mFavoritesSet = new HashSet<>();

    public Favorites() {

    }

    public Favorites(Set<String> extFavorites, String idUser) {
        try {
            setFavorites(extFavorites);
        } catch (ParseException e) {
            LogHelper.log(TAG, "Error while setting Favorites!" + e.getMessage());
        }
        setIdUser(idUser);
    }

    public void setFavorites(Set<String> mFavoritesSet) throws ParseException {
        JSONArray jsonFavorites = new JSONArray(mFavoritesSet);
        put(TAGFav, jsonFavorites);
        save();
    }

    public String getIdUser() {
        return getString("idUser");
    }

    public void setIdUser(String idUser) {
        put("idUser", idUser);
    }

    public Set<String> getFavoritesFromLocal() {
        return mFavoritesSet;
    }

    public boolean containsUrl(String url) {
        return mFavoritesSet.contains(url);
    }

    public void addUrlToLocal(String newUrl) {
        mFavoritesSet.add(newUrl);
    }

    public void deleteUrlToLocal(String url) {
        mFavoritesSet.remove(url);
    }

    public Set<String> getFavoritesFromServer() {
        JSONArray urlArray = getJSONArray(TAGFav);
        Set<String> favoritesSet = new HashSet<>();

        if (urlArray == null) {
            LogHelper.log(TAG, "Error parsing the favoritesList array from JSON");
            return favoritesSet;
        }

        for (int i = 0; i < urlArray.length(); i++) {
            try {
                favoritesSet.add(urlArray.getString(i));
            } catch (JSONException e) {
                LogHelper.log(TAG, e.getMessage());
            }
        }
        return favoritesSet;
    }

    public void synchronizeFromServer() {
        if (PInterface.INST.getProxy().internetAvailable()) {
            mFavoritesSet = getFavoritesFromServer();
        }

    }

    public void synchronizeServer() {
        if (PInterface.INST.getProxy().internetAvailable()) {
            PInterface.INST.overrideFavorites(getIdUser(), mFavoritesSet);
        }
    }
}
