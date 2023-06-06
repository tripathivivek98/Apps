package com.vivektripathi.tictactoe.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivektripathi.tictactoe.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    boolean gameActive = true;

    //Player Representation::
    // 0 - X
    // 1 - O

    int activePlayer = 0;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    // State Representation::
    // 0 - X
    // 1 - O
    // 2 - Null

    //Array of win positions
    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6},
            {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    public static int counter = 0;

    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);

    }

    public void playerTap(View view) {
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());
        //Game reset function will be called
        //If someone wins or all boxes are full
        if (!gameActive) {
            gameReset(view);
            counter = 0;
        }
        //if the tapped image is empty
        if (gameState[tappedImage] == 2) {
            //Increase the counter after every tap
            counter++;
            if (counter == 9) {
                //Reset the game
                gameActive = false;
            }
            //Mark this position
            gameState[tappedImage] = activePlayer;

            //Give a motion effect to the image
            img.setTranslationY(-1000f);

            //Change the active player from 0 to 1 or 1 to 0
            if (activePlayer == 0) {
                //set the image of X
                img.setImageResource(R.drawable.x);
                activePlayer = 1;

                //Change the status
                status.setText("O's turn, tap to play");

            } else {
                //Set the image O
                img.setImageResource(R.drawable.o);
                activePlayer = 0;
                status.setText("X's turn, tap to play");
            }
            img.animate().translationYBy(1000f).setDuration(300);
        }
        int flag = 0;

        //check if any player has won
        if (counter > 5) {
            for (int[] winPosition : winPositions) {
                if (gameState[winPosition[0]] == gameState[winPosition[1]] &&
                        gameState[winPosition[1]] == gameState[winPosition[2]] && gameState[winPosition[0]] != 2) {
                    flag = 1;
                    String winnerStr;
                    gameActive = false;
                    if (gameState[winPosition[0]] == 0) {
                        winnerStr = "X has won";
                    } else {
                        winnerStr = "O has won";
                    }
                    //Announce Winner
                    status.setText(winnerStr);

                }
            }

            //set the status if the match is Draw
            if (counter == 9 && flag == 0) {
                status.setText("Match Draw");
            }
        }
    }

    public void gameReset(View view) {
        gameActive = true;
        activePlayer = 0;
        Arrays.fill(gameState, 2);
        //Remove all images from view
        ((ImageView) findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView8)).setImageResource(0);

        status.setText("X's Turn - Tap to play");
    }
}