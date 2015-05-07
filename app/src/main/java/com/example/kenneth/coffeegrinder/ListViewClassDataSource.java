package com.example.kenneth.coffeegrinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 04-05-2015.
 */
public class ListViewClassDataSource {
    private SQLiteDatabase database;
    private DatabaseImpl dbHelper;
    private String[] allColumns = {DatabaseImpl.COLUMN_ID, DatabaseImpl.COLUMN_LISTVIEWCLASS};

    public ListViewClassDataSource(Context context){
        dbHelper = new DatabaseImpl(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public ListViewClass createListViewClass(String listViewClass){
        ContentValues values = new ContentValues();
        values.put(DatabaseImpl.COLUMN_LISTVIEWCLASS, listViewClass);
        long insertId = database.insert(DatabaseImpl.TABLE_LISTVIEWCLASS, null, values);
        Cursor cursor = database.query(DatabaseImpl.TABLE_LISTVIEWCLASS, allColumns, DatabaseImpl.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        ListViewClass newListViewClass = cursorToComment(cursor);
        cursor.close();

        return newListViewClass;
    }

    public void deleteListViewClass(ListViewClass listViewClass){
        long id = listViewClass.getId();
        System.out.println("ListViewClass deleted with id: " + id);
        database.delete(DatabaseImpl.TABLE_LISTVIEWCLASS, DatabaseImpl.COLUMN_ID
                + " = " + id, null);
    }


    public List<ListViewClass> getAllListViewClasses(){
        List<ListViewClass> listViewClasses = new ArrayList<ListViewClass>();

        Cursor cursor = database.query(DatabaseImpl.TABLE_LISTVIEWCLASS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            ListViewClass listViewClass = cursorToComment(cursor);
            listViewClasses.add(listViewClass);
            cursor.moveToNext();
        }

        cursor.close();
        return listViewClasses;
    }

    private ListViewClass cursorToComment(Cursor cursor){
        ListViewClass listViewClass = new ListViewClass("", "", 0);
        listViewClass.setId((int)(cursor.getLong(0)));
        listViewClass.setName(cursor.getString(1));
        listViewClass.setDescription(cursor.getString(1));
        return listViewClass;
    }
}
