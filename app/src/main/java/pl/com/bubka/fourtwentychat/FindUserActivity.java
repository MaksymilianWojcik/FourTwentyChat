package pl.com.bubka.fourtwentychat;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter userListAdapter;
    RecyclerView.LayoutManager layoutManager;

    private ArrayList<UserObject> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        userList = new ArrayList<>();

        initRecyclerView();
        getContactList();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.usersList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        userListAdapter = new UserListAdapter(userList);
        recyclerView.setAdapter(userListAdapter);
    }

    private void getContactList(){
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            UserObject userObject = new UserObject(name, phone);
            userList.add(userObject);
            userListAdapter.notifyDataSetChanged();
        }
    }
}
