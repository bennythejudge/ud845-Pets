/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {


    private PetDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new PetDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                Log.d("onOptionsItemSelected", "dummy data!!!!");
                insertPets();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                Log.d("catalog_activity", "about to truncate the pets table");
                delete_all_rows();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPets() {
        long newRowId;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String name = randomIdentifier();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, name);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);
        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, name);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "bastardino");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_FEMALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 5);
        newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        Log.d("insertPet","just added a row" );
    }

    /****
     * delete_all_rows
     * Delete all rows in the pets tables
     * cleanup the db
     */
    private void delete_all_rows() {
        Log.d("delete_all_rows", "trying to delete all row");
        // Create database helper
        PetDbHelper mDbHelper = new PetDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(PetContract.PetEntry.TABLE_NAME, null, null);
        db.close();
    }

    // class variable
    final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    final java.util.Random rand = new java.util.Random();
    // consider using a Map<String,Boolean> to say whether the identifier is being used or not
    final Set<String> identifiers = new HashSet<String>();
    public String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+5;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        PetDbHelper mDbHelper = new PetDbHelper(this);

        int idColumnIndex = 0;
        int nameColumnIndex = 1;
        int breedColumnIndex = 2;
        int genderColumnIndex = 3;
        int weightColumIndex = 4;

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = null;

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                PetContract.PetEntry.COLUMN_PET_NAME + " DESC";

        Cursor cursor = db.query(
                PetContract.PetEntry.TABLE_NAME,
                projection,              // The array of columns to return (pass null to get all)
                selection,               // The columns for the WHERE clause
                null,       // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );
        Log.d("displayDatabaseInfo", "after query");
        try {
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            if (cursor.getCount() > 0) {
                displayView.setText("Number of rows in pets database table: " + cursor.getCount());
                displayView.append(("\n" + "_id" + " - " + "Name" + " - " + "Breed" + " - " + "Gender" + " - " + " Weight"));
                while (cursor.moveToNext()) {
                    int currentID = cursor.getInt(idColumnIndex);
                    String currentName = cursor.getString(nameColumnIndex);
                    String currentBreed = cursor.getString(breedColumnIndex);
                    int currentGenderInt = cursor.getInt(genderColumnIndex);
                    String currentGender;
                    if (currentGenderInt == 1) {
                        currentGender = "Male";
                    } else if (currentGenderInt == 2) {
                        currentGender = "Female";
                    } else {
                        currentGender = "Unknown";
                    }
                    String currentWeight = cursor.getString(weightColumIndex);
                    displayView.append(("\n" + currentID + " - " +
                            currentName + " - " + currentBreed + " - " +
                            currentGender + " - " + currentWeight));
                }
            } else {
                Toast.makeText(this,"No pets found in DB", Toast.LENGTH_LONG ).show();
                displayView.setText("Number of rows in pets database table: " + cursor.getCount());
            }
        } finally {
            cursor.close();
        }
    }
}
