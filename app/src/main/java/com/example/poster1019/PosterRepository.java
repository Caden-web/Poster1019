package com.example.poster1019;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place that owns the app's movie data: all posters, the selection, and the favorites.
 */
public class PosterRepository {

    // Notified whenever favorites are added or removed (Favorite List, badge).
    public interface FavoritesListener{
        void onFavoritesChanged();
    }

    private static PosterRepository instance;

    private final List<Posters> allPosters;
    private final FavoritesStore favoritesStore;
    private final List<FavoritesListener> favoritesListeners = new ArrayList<>();

    // Creates the single instance on first use. It keeps the application context,
    // never an Activity: holding an Activity in a singleton would leak it.
    public static synchronized PosterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PosterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private PosterRepository(Context appContext) {
        allPosters = Collections.unmodifiableList(createPosters());
        favoritesStore = new FavoritesStore(appContext);

        // Forget saved favorites whose poster no longer exists in the app.
        List<String> validIds = new ArrayList<>();
        for (Posters poster : allPosters) {
            validIds.add(poster.id);
        }
        favoritesStore.retainOnly(validIds);
    }

    public List<Posters> getAllPosters(){
        return allPosters;
    }

    public Posters findById(String id){
        for(Posters posters:allPosters){
            if(posters.id.equals(id)){
                return posters;
            }
        }
        return null;
    }

    // deselects every poster
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    //get favorite posters, in the order they were added
    public List<Posters> getFavorites(){
        List<Posters> favorites = new ArrayList<>();
        for (String id : favoritesStore.getIds()) {
            Posters poster = findById(id);
            if (poster != null) {
                favorites.add(poster);
            }
        }
        return favorites;
    }

    public int getFavoriteCount(){
        return favoritesStore.getIds().size();
    }

    // Adds posters to favorites, skipping ones already there.
    // Returns how many were new, so the page can show the right message.
    public int addFavorites(List<Posters> posters) {
        List<String> ids = new ArrayList<>();
        for (Posters poster : posters) {
            ids.add(poster.id);
        }
        int added = favoritesStore.addAll(ids);
        if (added > 0) {
            notifyFavoritesChanged();
        }
        return added;
    }

    // Removes one favorite. Returns true if it was a favorite.
    public boolean removeFavorite(String id) {
        boolean removed = favoritesStore.remove(id);
        if (removed) {
            notifyFavoritesChanged();
        }
        return removed;
    }

    // The listener (observer) pattern: pages register to hear about changes.
    // Whoever adds a listener must remove it again (onDestroyView / onDestroy).
    public void addFavoritesListener(FavoritesListener listener) {
        if (!favoritesListeners.contains(listener)) {
            favoritesListeners.add(listener);
        }
    }

    public void removeFavoritesListener(FavoritesListener listener) {
        favoritesListeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }


    /**
     * Creates the five custom movie posters displayed in the app.
     * Each poster contains a unique ID, image, title, rating,
     * director, and story description.
     */
    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        // Create the Interstellar movie poster.
        Posters interstellar = new Posters();
        interstellar.id = "interstellar";
        interstellar.image = R.drawable.interstellar;
        interstellar.name = "Interstellar";
        interstellar.rating = 5.0f;
        interstellar.createdBy = "Christopher Nolan";
        interstellar.story = "A team of astronauts travels through a wormhole to find a new home for humanity.";
        posters.add(interstellar);

        // Create the Inception movie poster.
        Posters inception = new Posters();
        inception.id = "inception";
        inception.image = R.drawable.inception;
        inception.name = "Inception";
        inception.rating = 4.8f;
        inception.createdBy = "Christopher Nolan";
        inception.story = "A skilled thief enters people's dreams to steal secrets and plant ideas.";
        posters.add(inception);

        // Create The Dark Knight movie poster.
        Posters darkKnight = new Posters();
        darkKnight.id = "dark_knight";
        darkKnight.image = R.drawable.dark_knight;
        darkKnight.name = "The Dark Knight";
        darkKnight.rating = 4.9f;
        darkKnight.createdBy = "Christopher Nolan";
        darkKnight.story = "Batman faces the Joker, a criminal mastermind who threatens Gotham City.";
        posters.add(darkKnight);

        // Create the Dune movie poster.
        Posters dune = new Posters();
        dune.id = "dune";
        dune.image = R.drawable.dune;
        dune.name = "Dune";
        dune.rating = 4.6f;
        dune.createdBy = "Denis Villeneuve";
        dune.story = "Paul Atreides travels to the dangerous desert planet Arrakis, where his destiny awaits.";
        posters.add(dune);

        // Create the Oppenheimer movie poster.
        Posters oppenheimer = new Posters();
        oppenheimer.id = "oppenheimer";
        oppenheimer.image = R.drawable.oppenheimer;
        oppenheimer.name = "Oppenheimer";
        oppenheimer.rating = 4.8f;
        oppenheimer.createdBy = "Christopher Nolan";
        oppenheimer.story = "The story of J. Robert Oppenheimer and his role in developing the atomic bomb.";
        posters.add(oppenheimer);

        return posters;
    }



}
