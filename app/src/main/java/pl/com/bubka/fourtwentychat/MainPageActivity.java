package pl.com.bubka.fourtwentychat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    Button logoutButton, findUserButton;
    RecyclerView chatListRecyclerView;
    RecyclerView.Adapter chatListAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ChatObject> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        logoutButton = findViewById(R.id.logoutButton);
        findUserButton = findViewById(R.id.findUsersButton);

        chatList = new ArrayList<>();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainPageActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });

        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPageActivity.this, FindUserActivity.class));
            }
        });

        getPermissions();
        initRecyclerView();
        getChats();
    }

    private void getChats(){
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference()
                .child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        //ValueEvent will always be listening and checking for changes
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                     for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                         ChatObject chatObject = new ChatObject(childSnapshot.getKey());

                         boolean exist = false;
                         for(ChatObject chatObject1 : chatList) {
                             if (chatObject1.getChatId().equals(chatObject.getChatId())) {
                                 exist = true;
                             }
                         }
                             if(exist){
                                 continue;
                             }
                         chatList.add(chatObject);
                         chatListAdapter.notifyDataSetChanged();
                         }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initRecyclerView() {
        chatListRecyclerView = findViewById(R.id.chatList);
        chatListRecyclerView.setNestedScrollingEnabled(false);
        chatListRecyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        chatListRecyclerView.setLayoutManager(layoutManager);
        chatListAdapter = new ChatListAdapter(chatList);
        chatListRecyclerView.setAdapter(chatListAdapter);
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
