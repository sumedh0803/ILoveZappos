package com.example.ilovezappos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ilovezappos.Fragments.AsksFragment;
import com.example.ilovezappos.Fragments.BidsFragment;
import com.example.ilovezappos.Fragments.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TransactionFragment()).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFrag = null;
                    switch (menuItem.getItemId())
                    {
                        case R.id.txn: selectedFrag = new TransactionFragment();break;
                        case R.id.asks: selectedFrag = new AsksFragment(); break;
                        case R.id.bids: selectedFrag = new BidsFragment(); break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFrag).commit();
                    return true;
                }
            };
}
