package io.github.hidroh.materialistic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;

import org.robolectric.RuntimeEnvironment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.hidroh.materialistic.data.FavoriteManager;
import io.github.hidroh.materialistic.data.FeedbackClient;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.SessionManager;
import io.github.hidroh.materialistic.test.TestFavoriteActivity;
import io.github.hidroh.materialistic.test.TestInjectableActivity;
import io.github.hidroh.materialistic.test.TestListActivity;
import io.github.hidroh.materialistic.test.WebActivity;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module(
        injects = {
                // source classes
                SettingsActivity.class,
                AskActivity.class,
                AboutActivity.class,
                FavoriteActivity.class,
                ItemActivity.class,
                JobsActivity.class,
                ListActivity.class,
                NewActivity.class,
                SearchActivity.class,
                ShowActivity.class,
                WebActivity.class,
                FavoriteFragment.class,
                ItemFragment.class,
                ListFragment.class,
                WebFragment.class,
                DrawerFragment.class,
                // test classes
                SettingsActivityTest.class,
                ItemActivityTest.class,
                ItemFragmentMultiPageTest.class,
                ItemFragmentSinglePageTest.class,
                TestInjectableActivity.class,
                TestListActivity.class,
                io.github.hidroh.materialistic.test.ListActivity.class,
                ListFragmentViewHolderTest.class,
                FavoriteActivityTest.class,
                FavoriteActivityEmptyTest.class,
                TestFavoriteActivity.class,
                WebFragmentLocalTest.class,
                WebFragmentTest.class,
                FeedbackTest.class
        },
        library = true,
        overrides = true
)
public class TestActivityModule {
    private final ItemManager hackerNewsClient = mock(ItemManager.class);
    private final ItemManager algoliaClient = mock(ItemManager.class);
    private final FavoriteManager favoriteManager = mock(FavoriteManager.class);
    private final SessionManager sessionManager = mock(SessionManager.class);
    private final SearchView searchView = mock(SearchView.class);
    private final FeedbackClient feedbackClient = mock(FeedbackClient.class);

    @Provides @Singleton @Named(ActivityModule.HN)
    public ItemManager provideHackerNewsClient() {
        return hackerNewsClient;
    }

    @Provides @Singleton @Named(ActivityModule.ALGOLIA)
    public ItemManager provideAlgoliaClient() {
        return algoliaClient;
    }

    @Provides @Singleton
    public FavoriteManager provideFavoriteManager() {
        return favoriteManager;
    }

    @Provides @Singleton
    public SessionManager provideSessionManager() {
        return sessionManager;
    }

    @Provides @Singleton
    public FeedbackClient provideFeedbackClient() {
        return feedbackClient;
    }

    @Provides @Singleton
    public ActionViewResolver provideActionViewResolver() {
        ActionViewResolver resolver = mock(ActionViewResolver.class);
        when(resolver.getActionView(any(MenuItem.class))).thenReturn(searchView);
        return resolver;
    }

    @Provides
    public AlertDialogBuilder provideAlertDialogBuilder() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RuntimeEnvironment.application);
        return new AlertDialogBuilder() {
            @Override
            public AlertDialogBuilder setMessage(@StringRes int messageId) {
                builder.setMessage(messageId);
                return this;
            }

            @Override
            public AlertDialogBuilder setView(View view) {
                builder.setView(view);
                return this;
            }

            @Override
            public AlertDialogBuilder setNegativeButton(@StringRes int textId,
                                                        DialogInterface.OnClickListener listener) {
                builder.setNegativeButton(textId, listener);
                return this;
            }

            @Override
            public AlertDialogBuilder setPositiveButton(@StringRes int textId,
                                                        DialogInterface.OnClickListener listener) {
                builder.setPositiveButton(textId, listener);
                return this;
            }

            @Override
            public Dialog create() {
                return builder.create();
            }

            @Override
            public Dialog show() {
                return builder.show();
            }
        };
    }
}
