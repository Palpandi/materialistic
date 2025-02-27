package io.github.hidroh.materialistic.test;

import io.github.hidroh.materialistic.InjectableActivity;
import io.github.hidroh.materialistic.MultiPaneListener;
import io.github.hidroh.materialistic.data.ItemManager;

import static org.mockito.Mockito.mock;

public class ListActivity extends InjectableActivity implements MultiPaneListener {
    public MultiPaneListener multiPaneListener = mock(MultiPaneListener.class);

    @Override
    public void onItemSelected(ItemManager.WebItem story) {
        multiPaneListener.onItemSelected(story);
    }

    @Override
    public void clearSelection() {
        multiPaneListener.clearSelection();
    }

    @Override
    public ItemManager.WebItem getSelectedItem() {
        return multiPaneListener.getSelectedItem();
    }
}
