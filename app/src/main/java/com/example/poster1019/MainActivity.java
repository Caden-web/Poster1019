
package com.example.poster1019;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_PAGE = "current_page";

    // Unique tags used to identify each page's Fragment.
    private static final String TAG_PAGE_MAIN = "page_main";
    private static final String TAG_PAGE_FAVORITES = "page_favorites";
    private static final String TAG_PAGE_CONTACT = "page_contact";
    private static final String TAG_PAGE_SPOTLIGHT = "page_spotlight";

    // Navigation components.
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;

    // Tracks the currently selected page.
    private int currentPageId = R.id.nav_main;

    // Repository responsible for movie posters and favorites.
    private PosterRepository posterRepository;

    // Listener that updates the favorites badge when favorites change.
    private final PosterRepository.FavoritesListener favoritesBadgeListener =
            this::updateFavoritesBadge;

    /**
     * Updates the badge on the Favorite List navigation item
     * to display the current number of saved movies.
     */
    private void updateFavoritesBadge() {

        int count = posterRepository.getFavoriteCount();

        BadgeDrawable badge =
                bottomNavigation.getOrCreateBadge(R.id.nav_favorites);

        badge.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorBadge)
        );

        badge.setBadgeTextColor(
                ContextCompat.getColor(this, R.color.colorTextIcons)
        );

        badge.setNumber(count);
        badge.setVisible(count > 0);
    }

    /**
     * Initializes the Activity, navigation menus, toolbar,
     * all four Fragments, and the favorites badge.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Find the navigation components.
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Set the MaterialToolbar as the Activity's ActionBar.
        setSupportActionBar(toolbar);

        // Connect the toolbar hamburger icon to the side drawer.
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        /**
         * Handles navigation when the user selects a page
         * from the side navigation drawer.
         */
        navigationView.setNavigationItemSelectedListener(item -> {
            showPage(item.getItemId());
            return true;
        });

        /**
         * Handles navigation when the user selects a page
         * from the bottom navigation bar.
         */
        bottomNavigation.setOnItemSelectedListener(item -> {
            showPage(item.getItemId());
            return true;
        });

        // Get layout components used for system bar spacing.
        final AppBarLayout appBarLayout =
                findViewById(R.id.appBarLayout);

        final View fragmentContainer =
                findViewById(R.id.fragmentContainer);

        final View drawerHeader =
                navigationView.getHeaderView(0);

        final int drawerHeaderPaddingTop =
                drawerHeader.getPaddingTop();

        /**
         * Adjusts layout padding so content does not overlap
         * the system status bar, navigation bar, or display cutout.
         */
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                    );

                    appBarLayout.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            0
                    );

                    fragmentContainer.setPadding(
                            bars.left,
                            0,
                            bars.right,
                            0
                    );

                    drawerHeader.setPadding(
                            drawerHeader.getPaddingLeft(),
                            drawerHeaderPaddingTop + bars.top,
                            drawerHeader.getPaddingRight(),
                            drawerHeader.getPaddingBottom()
                    );

                    return insets;
                }
        );

        /**
         * Creates the four application pages on first launch.
         * Existing Fragments are restored automatically
         * when the Activity is recreated.
         */
        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()

                    .add(
                            R.id.fragmentContainer,
                            new PostersFragment(),
                            TAG_PAGE_MAIN
                    )

                    .add(
                            R.id.fragmentContainer,
                            new FavoritesFragment(),
                            TAG_PAGE_FAVORITES
                    )

                    .add(
                            R.id.fragmentContainer,
                            new ContactFragment(),
                            TAG_PAGE_CONTACT
                    )

                    .add(
                            R.id.fragmentContainer,
                            new SpotlightFragment(),
                            TAG_PAGE_SPOTLIGHT
                    )

                    .commitNow();

        } else {

            currentPageId = savedInstanceState.getInt(
                    KEY_CURRENT_PAGE,
                    R.id.nav_main
            );
        }

        // Display the selected page.
        showPage(currentPageId);

        // Initialize the poster repository.
        posterRepository = PosterRepository.getInstance(this);

        // Register the listener for favorite count updates.
        posterRepository.addFavoritesListener(favoritesBadgeListener);

        // Display the initial favorites badge.
        updateFavoritesBadge();
    }

    /**
     * Removes the favorites listener when the Activity
     * is destroyed to prevent memory leaks.
     */
    @Override
    protected void onDestroy() {

        if (posterRepository != null) {
            posterRepository.removeFavoritesListener(
                    favoritesBadgeListener
            );
        }

        super.onDestroy();
    }

    /**
     * Saves the currently selected page so that rotating
     * the device does not reset navigation to the Main Page.
     */
    @Override
    protected void onSaveInstanceState(
            @NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt(
                KEY_CURRENT_PAGE,
                currentPageId
        );
    }

    /**
     * Displays the selected Fragment while hiding the other
     * three pages. Also updates the toolbar title and
     * synchronizes both navigation menus.
     *
     * @param pageId The resource ID of the selected page.
     */
    private void showPage(int pageId) {

        currentPageId = pageId;

        FragmentManager fragmentManager =
                getSupportFragmentManager();

        FragmentTransaction transaction =
                fragmentManager.beginTransaction();

        // Determine which Fragment should be visible.
        String visibleTag = tagForPage(pageId);

        // List of all four Fragment tags.
        String[] pageTags = {
                TAG_PAGE_MAIN,
                TAG_PAGE_FAVORITES,
                TAG_PAGE_CONTACT,
                TAG_PAGE_SPOTLIGHT
        };

        // Show the selected page and hide the others.
        for (String tag : pageTags) {

            Fragment page =
                    fragmentManager.findFragmentByTag(tag);

            if (page == null) {
                continue;
            }

            if (tag.equals(visibleTag)) {
                transaction.show(page);
            } else {
                transaction.hide(page);
            }
        }

        // Apply the Fragment visibility changes.
        transaction.commit();

        // Update the toolbar title.
        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(
                    titleForPage(pageId)
            );
        }

        // Update the selected item in the side drawer.
        navigationView.setCheckedItem(pageId);

        // Update the selected item in the bottom navigation.
        if (bottomNavigation.getMenu().findItem(pageId) != null) {

            bottomNavigation.getMenu()
                    .findItem(pageId)
                    .setChecked(true);
        }

        // Close the side drawer after selecting a page.
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Returns the appropriate toolbar title
     * for the selected navigation page.
     *
     * @param pageId The selected navigation item ID.
     * @return The string resource ID for the page title.
     */
    private int titleForPage(int pageId) {

        if (pageId == R.id.nav_favorites) {

            return R.string.title_favorites;

        } else if (pageId == R.id.nav_contact) {

            return R.string.title_contact;

        } else if (pageId == R.id.nav_spotlight) {

            return R.string.title_spotlight;
        }

        return R.string.posters;
    }

    /**
     * Maps each navigation item to its corresponding
     * Fragment tag so the correct page can be displayed.
     *
     * @param pageId The selected navigation item ID.
     * @return The Fragment tag for the selected page.
     */
    private String tagForPage(int pageId) {

        if (pageId == R.id.nav_favorites) {

            return TAG_PAGE_FAVORITES;

        } else if (pageId == R.id.nav_contact) {

            return TAG_PAGE_CONTACT;

        } else if (pageId == R.id.nav_spotlight) {

            return TAG_PAGE_SPOTLIGHT;
        }

        return TAG_PAGE_MAIN;
    }
}
