package pl.com.bubka.fourtwentychat;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter userListAdapter;
    RecyclerView.LayoutManager layoutManager;

    private ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        userList = new ArrayList<>();
        contactList = new ArrayList<>();

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
        String prefix = getCountryIso();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (!String.valueOf(phone.charAt(0)).equals("+")) {
                phone = prefix + phone;
            }

            UserObject userObject = new UserObject(name, phone, "");
            contactList.add(userObject);
//            userListAdapter.notifyDataSetChanged(); not needed anymore

            getUserDetails(userObject);

        }
    }

    private void getUserDetails(UserObject userObject) {
//        final String nameFromContacts = userObject.getName();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = userRef.orderByChild("phone").equalTo(userObject.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String phone = "";
                    String name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("phone").getValue() != null){
                            phone =  childSnapshot.child("phone").getValue().toString();
                        }
                        if(childSnapshot.child("name").getValue() != null){
                            name =  childSnapshot.child("name").getValue().toString();
                        }

                        UserObject mUser = new UserObject(name, phone, childSnapshot.getKey());

                        if(name.equals(phone)){
                            for(UserObject userContact : contactList){
                                if(userContact.getPhoneNumber().equals(mUser.getPhoneNumber())){
                                    mUser.setName(userContact.getName());
                                }
                            }
                        }

                        userList.add(mUser);
                        userListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryIso(){
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null){
            if(!telephonyManager.getNetworkCountryIso().isEmpty()){
                iso = telephonyManager.getNetworkCountryIso(); //PT, ENG, etc.
                return Iso2Phone.getPhone(iso);
            }
        }
        return iso;
    }
}
