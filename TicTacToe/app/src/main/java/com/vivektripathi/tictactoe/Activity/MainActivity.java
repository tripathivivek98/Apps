package com.vivektripathi.tictactoe.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.vivektripathi.tictactoe.R;

public class MainActivity extends AppCompatActivity {
boolean gameActive = true;

    //Player Representation::
    // 0 - X
    // 1 - O

    int activePlayer = 0;
    int[] gameState = {2,2,2,2,2,2,2,2,2};

    // State Representation::
    // 0 - X
    // 1 - O
    // 2 - Null

    //Array of win positions
    int[][] winPositions = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},
                        {1,4,7},{2,5,8},{0,4,8},{2,4,6}};

    public static int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playerTap(View view){

    }
}