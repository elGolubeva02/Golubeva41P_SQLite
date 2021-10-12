package com.example.myapplication;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;
//Главный класс
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button button1, button3;
    EditText Txt1, Txt2, Txt3;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Txt1 = (EditText) findViewById(R.id.Txt1);
        Txt2 = (EditText) findViewById(R.id.Txt2);
        Txt3 = (EditText) findViewById(R.id.Txt3);
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        UpdateTable();
    }

    public void UpdateTable()
    {
        Cursor cursor = database.query(DBHelper.TABLE_BOOKS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int authorIndex = cursor.getColumnIndex(DBHelper.KEY_AUTHOR);
            int phouseIndex = cursor.getColumnIndex(DBHelper.KEY_PHOUSE);
            TableLayout tblay = findViewById(R.id.tblay);
            tblay.removeAllViews();
            do {
                TableRow tblayrow = new TableRow(this);
                tblayrow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView outputid = new TextView(this);
                params.weight = 1.0f;
                outputid.setLayoutParams(params);
                outputid.setText(cursor.getString(idIndex));
                tblayrow.addView(outputid);
                TextView outputname = new TextView(this);
                params.weight = 3.0f;
                outputname.setLayoutParams(params);
                outputname.setText(cursor.getString(nameIndex));
                tblayrow.addView(outputname);
                TextView outputauthor = new TextView(this);
                params.weight = 3.0f;
                outputauthor.setLayoutParams(params);
                outputauthor.setText(cursor.getString(authorIndex));
                tblayrow.addView(outputauthor);
                TextView outputphouse = new TextView(this);
                params.weight = 3.0f;
                outputphouse.setLayoutParams(params);
                outputphouse.setText(cursor.getString(phouseIndex));
                tblayrow.addView(outputphouse);
                Button deletbt = new Button(this);
                deletbt.setOnClickListener(this);
                params.weight = 3.0f;
                deletbt.setLayoutParams(params);
                deletbt.setText("Удалить запись");
                deletbt.setId(cursor.getInt(idIndex));
                tblayrow.addView(deletbt);
                tblay.addView(tblayrow);


            } while (cursor.moveToNext());

        } else




        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                String name = Txt1.getText().toString();
                String author = Txt2.getText().toString();
                String phouse = Txt3.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_AUTHOR, author);
                contentValues.put(DBHelper.KEY_PHOUSE, phouse);
                database.insert(DBHelper.TABLE_BOOKS, null, contentValues);
                UpdateTable();
                Txt1.setText(null);
                Txt2.setText(null);
                Txt3.setText(null);
                break;
            case R.id.button3:
                database.delete(DBHelper.TABLE_BOOKS, null, null);
                TableLayout dbOutput = findViewById(R.id.tblay);
                dbOutput.removeAllViews();
                Txt1.setText(null);
                Txt2.setText(null);
                Txt3.setText(null);;
                UpdateTable();
                break;
            default:
                View outputDBRoe = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRoe.getParent();
                outputDB.removeView(outputDBRoe);
                outputDB.invalidate();
                database.delete(DBHelper.TABLE_BOOKS,DBHelper.KEY_ID + " = ?", new String[] {String.valueOf(v.getId())});
                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_BOOKS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int authorIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_AUTHOR);
                    int phouseIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_PHOUSE);
                    int realId = 1;
                    do {
                        if (cursorUpdater.getInt(idIndex) > realId) {
                            contentValues.put(DBHelper.KEY_ID, realId);
                            contentValues.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_AUTHOR, cursorUpdater.getString(authorIndex));
                            contentValues.put(DBHelper.KEY_PHOUSE, cursorUpdater.getString(phouseIndex));
                            database.replace(DBHelper.TABLE_BOOKS, null, contentValues);
                        }
                        realId++;
                    } while (cursorUpdater.moveToNext());
                    if (cursorUpdater.moveToLast() && v.getId()!=realId)
                    {
                        database.delete(DBHelper.TABLE_BOOKS, DBHelper.KEY_ID+ " = ?", new String[] {cursorUpdater.getString(idIndex)});
                    }
                  UpdateTable();
                }
                break;
        }
    }
}