COMP204-16B / COMP242-16B Assignment 5
======================================

Due on **Wednesday, 21^(st) September at 11:30pm**.


Android Sensors and Systems
===========================

The goal of this exercise is to become accustomed to using sensors in Android, and to become familiar with system and application interactions.

Android is described in the [documentation supplied by Google, found here](https://developer.android.com/index.html).


Preamble
========

1. Fork this repository using the button at the top of the page.
  * Set the visibility level of the project to Private.
2. Clone your new repository to your computer using Git.
3. Remember to commit and push regularly!


Overview
========

You will be building an application that experiments with the Android lifecycle. 
You will use your knowledge of the lifecycle to interact with the Android GPS and 
location services to determine if the GPS is available and if so, obtain the current
location. In addition, you will further your knowledge of layouts, and Intents to 
pass meaningful data to other applications.

Pictured below is what your interface should look like at the end of this assignment.

![An image of the expected interface](img/interface.png)


Task
====

Complete as much of the task as you can. Some steps are more complex than others, you may wish to complete the tasks out of order to help keep momentum.

* To begin, create a new project in your cloned repository
  * Use API level 23 (Marshmallow)
  * Name your project **LocationApp**
  * Start with a blank activity
* Add the project, and a suitable gitignore file to your repository
  * Commit and push your code to your repository, remember to do this regularly
* Add code to your project that outputs messages to the **debug** log when a lifecycle method is called
  * Use the tag **Lifecycle** for these messages
  * Simply printing the name of the called method will be sufficient
  * Due to the imprecision of the `logcat` timestamps, you will want to call `Thread.sleep(10)` after your log messages so that they appear in the correct order
* Modify your layout so that it uses a linear layout with a vertical orientation
  * Remove the default `TextView` that is present
* Add two `TextView`s to your layout, side-by-side. Set the text for the left-hand TextView to say `Lifecycle:`
  * Hint: You can nest layouts with different orientations
* Update your code so that in addition to outputting to the debug log, each lifecycle method updates the right-hand TextView with the name of the method
  * As you now have a lot of duplicate code, create a helper function that outputs to the log and updates the TextView. Replace the duplicated code with calls to this function
* Adjust the styling of the TextViews so that they match the screenshot above
  * The exact spacings and text sizes are not important, but it should look as close to the example as you can get it
* Duplicate your layout modifications to add a second row of TextFields
  * Set the new left-hand TextView text to `GPS Status`
  * Set the new right-hand TextView text to `Unknown`, colored orange
  * Ensure your chosen shade of orange is readable against the background color of the activity
* Create a helper method that can set the color and text of your new right-hand TextView
  * This should support 4 states; Unknown (Orange), Enabled (Green), Disabled (Red) and Unavailable (Blue)
  * Avoid using magic numbers or strings to determine which state should be set - An enum would be a good solution here
  * Ensure your chosen colors are readable against the background of the activity
* Modify your code so that your new helper function is called as soon as your code begins to run, setting the state to `Unknown`
* Add a check to your code to determine if a GPS device is present
  * Hint: Use [PackageManager](https://developer.android.com/reference/android/content/pm/PackageManager.html) to work this out
  * Remember to update the TextView
* Create a [LocationListener](https://developer.android.com/reference/android/location/LocationListener.html) to use with the GPS device
  * There were 3 methods for doing this discussed in the tutorial, you may use whichever method you prefer
  * Remember to add the appropriate items to the Manifest
* Register your LocationListener
  * Refer to the tutorial recordings for how and where to do this
  * You will get an error about needing to check for permissions. Allow AndroidStudio to automatically insert the appropriate code
* Relocate your permission checking code so that it will be run when the application starts
  * Enhance this code so that if the appropriate permissions are not set, you request them
  * Remember to update your TextView with the appropriate message and colors at each stage
* Extend your helper method so that a field is set indicating whether GPS is enabled or not
  * Surround your LocationListener registration code with a conditional that the registration will only occur is the GPS is Enabled
  * You will need to surround the registration call itself with a try-catch that catches `SecurityException` to clear the error
* In your layout, add 4 new TextViews laid out as shown in the example screenshot
  * `Lat` and `Lon` are labels, `Lat_val` and `Lon_val` will need to be updated, so plan accordingly
  * Style the TextViews so that they as closely as possible match the image above
* Load [the supplied GPS Trace](trace/2257995.gpx) into your AVD in as described in the 
[Supplemental - Location Mocking in AVDs](http://coursecast.its.waikato.ac.nz/Panopto/Pages/Viewer.aspx?id=57b66922-0b0f-4839-ba54-f06fdada4516)
recording.
  * Start this playing so that you can see locations in the next steps
* Update your LocationListener code so that when the GPS location changes, the `Lat_val` and `Lon_val` TextViews show the latitude and longitude of the location respectively
* Add code to unregister your LocationListener 
* Add a [Spinner](https://developer.android.com/reference/android/widget/Spinner.html) to your layout as shown in the example
* Add the following code to your project

```java
// Put me in my own file

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
```

```java
// Put me in the top of the Activity class
List<SpinnerItem> spinnerList = Arrays.asList(
    // Items created here will be initialized into the list
    new SpinnerItem("An Item", Uri.parse("")), 
    new SpinnerItem("B", Uri.parse(""))
);
```

* Bind up your spinner to the list created in the copied code in the onCreate method with the following

```java
((Spinner) findViewById(R.id.spinner_id)).setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList));
```

* Add a button to your layout as shown in the example
  * Bind an onClick method to the button through the layout
* Remove all test data from the `spinnerList`
* Insert a SpinnerItem into the `spinnerList` with the label `From GPS` and a null uri
* Determine the appropriate spinner listener to use if you want to find when an item is selected
  * Implement this listener
  * Detect if the item selected is the `From GPS` item (is the uri null)
  * If the `From GPS` item is selected but GPS is not enabled, disable the button
  * Otherwise, leave it enabled
* Edit your button onClick event to launch an intent to display a location in Google Maps
  * [Documentation on this can be found here](https://developers.google.com/maps/documentation/android-api/intents)
  * If the `From GPS` item is selected in the spinner, open Google Maps to the location reported by the GPS
  * Otherwise, start the intent with the uri found in the SpinnerItem
* Add 4 new `SpinnerItem`s to the spinnerList for locations of your choice
  * Utilize the different styles of uris for sending locations to Google Map
  * We expect to see at least one by lat/lon, and one by address
* Go over your layout, and make sure it is as close to the example as you can get it
* Submit!


Submission
==========

Upload a zipped copy of your repository to [moodle in the assignment submission](https://elearn.waikato.ac.nz/mod/assign/view.php?id=572148). 
Please download the zip from GitLab using the download button in the top right 
of the project view rather than zipping it from the copy on your local hard drive.


Grading
=======

| Weighting | Allocated to |
|:---------:|--------------|
| 10% | Correct repository usage and settings |
| 90% | Task, allocated based on completion and correctness |
