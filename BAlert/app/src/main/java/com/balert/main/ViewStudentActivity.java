
package com.balert.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ViewStudentActivity extends Activity implements OnClickListener {
    
    private Button addButton;
    
    private ArrayList<HashMap<String, Object>> list;
    
    private AlertDialog.Builder optionsBuilder, deleteBuilder;
    
    private DatabaseHelper databaseHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_student);
        
        addButton = (Button)findViewById(R.id.buttonAddNew);
        
        addButton.setOnClickListener(this);
        
        list = new ArrayList<HashMap<String, Object>>();
        
        databaseHelper = new DatabaseHelper(this);
        
        ListView listView = (ListView)findViewById(R.id.listViewStudents);
        
        Cursor cursor = databaseHelper.getStudentDetails(-1);
        
        if (cursor != null && cursor.moveToNext()) {
            
            int i = 1;
            
            do {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("number", i + "");
                hashMap.put("id", cursor.getInt(0) + "");
                hashMap.put("regNo",
                        "Name: " + cursor.getString(1) + "(Reg.No: " + cursor.getString(2) + ")");
                hashMap.put("class", "Class: " + cursor.getString(3) + "" + cursor.getString(5));
                
                list.add(hashMap);
                i++;
            } while (cursor.moveToNext());
            cursor.close();
            
            optionsBuilder = new AlertDialog.Builder(this);
            deleteBuilder = new AlertDialog.Builder(this);
            
            listView.setAdapter(new SimpleAdapter(this, list, R.layout.student_entry_view,
                    new String[] {
                            "number", "id", "regNo", "class",
                    }, new int[] {
                            R.id.textViewNo, R.id.textViewId, R.id.textViewRegNo,
                            R.id.textViewClass
                    }));
            
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    
                    final int studId = Integer.parseInt(((TextView)view
                            .findViewById(R.id.textViewId)).getText().toString());
                    
                    optionsBuilder.setTitle("Options");
                    optionsBuilder.setMessage("select the option");
                    
                    optionsBuilder.setPositiveButton("Delete",
                            new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteBuilder.setTitle("Confirm");
                                    deleteBuilder.setMessage("Are u sure to delete this?");
                                    
                                    deleteBuilder.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    databaseHelper.delete(studId);
                                                    startActivity(getIntent());
                                                    finish();
                                                }
                                            });
                                    
                                    deleteBuilder.setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    deleteBuilder.show();
                                }
                            });
                    
                    optionsBuilder.setNeutralButton("Update",
                            new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewStudentActivity.this,
                                            UpdateStudentActivity.class).putExtra("studId", studId));
                                }
                            });
                    
                    optionsBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    optionsBuilder.show();
                }
            });
            
        } else {
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    new String[] {
                        "No records found!"
                    }));
            
        }
        
    }
    
    @Override
    public void onClick(View v) {
        
        if (v.equals(addButton)) {
            startActivity(new Intent(this, AddStudentActivity.class));
            
        }
    }
    
    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
