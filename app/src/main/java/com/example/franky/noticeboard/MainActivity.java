package com.example.franky.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase,userReference;
    private RecyclerView announcementList;
    private String userSubscription,userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Firebase database
        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        //RecyclerView
        announcementList = (RecyclerView) findViewById(R.id.recyclerView);
        announcementList.setHasFixedSize(true);
        announcementList.setLayoutManager(new LinearLayoutManager(this));

        if(user != null){
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String subscription = dataSnapshot.child("subscription").getValue(String.class);
                    userEmail = email.toString();
                    userSubscription = subscription.toString();
                    updateUI(userEmail,userSubscription);
                    String title = userSubscription +" Notice Board";

                    getSupportActionBar().setTitle(title);

                    //Getting Announcements
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(userSubscription);
                    mDatabase.keepSynced(true);

                    FirebaseRecyclerAdapter<studentAnnouncement,announcementViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<studentAnnouncement, announcementViewHolder>
                            (studentAnnouncement.class,R.layout.row,announcementViewHolder.class,mDatabase) {
                        @Override
                        protected void populateViewHolder(announcementViewHolder viewHolder, studentAnnouncement model, int position) {
                            viewHolder.setTitle(model.getTitle());
                            viewHolder.setBody(model.getBody());
                        }
                    };

                    announcementList.setAdapter(firebaseRecyclerAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void openDialog(){
        studentAnnouncementDialogue studentDialog = new studentAnnouncementDialogue();
        studentDialog.show(getSupportFragmentManager(),"Student Dialog");
    }

//    public void getAnnouncements(String subscription){
//    }





    public static class announcementViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public announcementViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            System.out.println(title);
            TextView announcement_title = (TextView)mView.findViewById(R.id.announcement_title);
            announcement_title.setText(title);
        }
        public void setBody(String body){
            System.out.println(body);
            TextView announcement_body = (TextView)mView.findViewById(R.id.announcement_body);
            announcement_body.setText(body);
        }
    }

    public void updateUI(String email,String subscription){

        System.out.println("In update Ui function");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView currentUserEmail = (TextView)hView.findViewById(R.id.currentUserEmail);
        TextView currentUserType = (TextView)hView.findViewById(R.id.currentUserType);
        currentUserEmail.setText(email);
        currentUserType.setText(subscription);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
