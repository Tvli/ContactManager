package org.intracode.contactmanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teng on 08/06/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactManager",
    TABLE_CONTACTS = "contacts",
    KEY_ID = "id",
    KEY_NAME = "name",
    KEY_PHONE = "phone",
    KEY_EMAIL = "email",
    KEY_ADDRESS = "address",
    KEY_IMAGEURI = "imageUri";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
//    Write create table statements
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_IMAGEURI + " TEXT)");
    }

    @Override
//    This method is called when table structure is changed or adding constraints to db.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
//        Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//        Create table again
        onCreate(db);
    }



//    CRUD methods below


//    Create new contact and add it to the database
    public void createContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

//        Insert the row created above
        db.insert(TABLE_CONTACTS, null, values);
//        Close database connection
        db.close();

    }

//       Read single contact row. It accepts id as parameter and will return matched row from the database
    public Contact getContact(int id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS,new String[]{KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS, KEY_IMAGEURI}, KEY_ID + "=?", new String[]{ String.valueOf(id) }, null, null,null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();

        return contact;
    }

//  Get the total number of records in the table
    public int getContactsCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

//    Update single contact in the database. Use contact instance as parameter
    public int updateContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        // Set new row's values
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

//        update this row by id
        return db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[]{ String.valueOf(contact.getId()) });

    }


//    Delete single contact from database
    public void deleteContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[]{ String.valueOf(contact.getId()) });
        db.close();
    }
    
    //A method to check duplicacy of email
    public int checkDuplicateNumber(String email){
        SQLiteDatabase db=this.getReadableDatabase();
        String check="SELECT * FROM "+TABLE_CONTACTS+" WHERE "+KEY_EMAIL+"="+ email;
        Cursor cursor=db.rawQuery(check,null);
        return cursor.getCount();
        //returns 0 if no duplicate found
    }
    


//
    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<Contact>();
//    Why get all contacts need a writable database?????
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

//        Loop through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
//                Add contact to list
                contacts.add(contact);

            }while (cursor.moveToNext());
        }

        return contacts;
    }


}
