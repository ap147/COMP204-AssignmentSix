package com.amarjot8.locationapp;

import android.net.Uri;

/**
 * Helper class for spinners. label will be shown in the spinner dialogue, and uri can be obtained from the
 * item selected listener by casting the selected item to SpinnerItem and accessing the field.
 */
public class SpinnerItem {
    public final String label;
    public final Uri uri;

    public SpinnerItem(String label, Uri uri) {
        this.label = label;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return this.label;
    }
}