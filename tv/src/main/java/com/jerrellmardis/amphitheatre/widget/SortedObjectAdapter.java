/*
 * Copyright (C) 2014 Jerrell Mardis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jerrellmardis.amphitheatre.widget;

import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by Jerrell Mardis on 8/16/14.
 */
public class SortedObjectAdapter extends ArrayObjectAdapter {

    private TreeSet<Object> mSortedItems;

    public SortedObjectAdapter(Comparator comparator, PresenterSelector presenterSelector) {
        super(presenterSelector);
        mSortedItems = new TreeSet<Object>(comparator);
    }

    public SortedObjectAdapter(Comparator comparator, Presenter presenter) {
        super(presenter);
        mSortedItems = new TreeSet<Object>(comparator);
    }

    public SortedObjectAdapter(Comparator comparator) {
        super();
        mSortedItems = new TreeSet<Object>(comparator);
    }

    /**
     * Adds an item to the set.
     *
     * @param item The item to add to the set.
     */
    public void add(Object item) {
        mSortedItems.add(item);
        super.add(mSortedItems.headSet(item).size(), item);
    }

    /**
     * Removes the item from the set.
     *
     * @param item The item to remove from the set.
     * @return True if the item was found and thus removed from the set.
     */
    public boolean remove(Object item) {
        mSortedItems.remove(item);
        return super.remove(item);
    }


    /**
     * Removes all items from this set, leaving it empty.
     */
    public void clear() {
        mSortedItems.clear();
        super.clear();
    }
}