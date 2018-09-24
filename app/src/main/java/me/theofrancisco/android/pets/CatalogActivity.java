package me.theofrancisco.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.theofrancisco.android.pets.data.PetContract.PetEntry;
import me.theofrancisco.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity (FloatingActionButton)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        Cursor c = null;
        String records = "reading db...";
        try {
            // Create and/or open a database to read from it
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            //Projection
            String[] projection = {
                    PetEntry._ID,
                    PetEntry.COLUMN_PET_NAME,
                    PetEntry.COLUMN_PET_BREED,
                    PetEntry.COLUMN_PET_GENDER,
                    PetEntry.COLUMN_PET_WEIGHT
            };
            //String selection = PetEntry.COLUMN_PET_GENDER + "=?";
            //String selectionArgs = new String[]{PetEntry.GENDER_FEMALE};
            c = db.query(PetEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null);
            records = "cursor count: " + c.getCount() + "\n";
            if (c.getCount() > 0) {
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    int id = c.getInt(0);
                    records = records.concat(Integer.toString(id)) + " ";
                    String name = c.getString(1);
                    records = records.concat(name) + " ";
                    String breed = c.getString(2);
                    records = records.concat(breed) + " ";
                    int codGender = c.getInt(3);
                    String gender;
                    switch (codGender) {
                        case 1: {
                            gender = " M ";
                            break;
                        }
                        case 2:
                            gender = " F ";
                        default:
                            gender = " U ";
                    }
                    int weight = c.getInt(4);
                    records = records.concat(name + " " + breed + gender + weight + "\n");
                    c.moveToNext();
                }
            } else {
                records = "no records found!";
            }

        } catch (Exception e) {
            records = records.concat("Error: ").concat(e.getMessage());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            if (c != null) c.close();
        }
        // Display the number of rows in the Cursor (which reflects the number of rows in the
        // pets table in the database).
        TextView displayView = findViewById(R.id.text_view_pet);
        //displayView.setText("Number of rows in pets database table: " + c.getCount());
        displayView.setText(records);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertDummyPet() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}