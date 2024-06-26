package com.example.assinmentandroid;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assinmentandroid.Logic.GameManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView[] main_IMG_hearts;
    private ExtendedFloatingActionButton main_FAB_left_arrow;
    private ExtendedFloatingActionButton main_FAB_right_arrow;
    private AppCompatImageView[][] main_matrix_IMG_barriers;
    private AppCompatImageView[] main_cols_IMG_cars;
    private GameManager gameManager;
    private static final long DELAY = 1000L;
    private long startTime;

    private boolean timerOn = false;

    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        gameManager = new GameManager(main_IMG_hearts.length, main_matrix_IMG_barriers.length, main_matrix_IMG_barriers[0].length);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void findViews() {
        //array of hearts
        main_IMG_hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };

        //arrows buttons
        main_FAB_left_arrow = findViewById(R.id.main_FAB_left_arrow);
        main_FAB_right_arrow = findViewById(R.id.main_FAB_right_arrow);

        //matrix of barriers
        main_matrix_IMG_barriers = new AppCompatImageView[][]{
                {
                        findViewById(R.id.main_matrix_pos_00),
                        findViewById(R.id.main_matrix_pos_01),
                        findViewById(R.id.main_matrix_pos_02)
                },
                {
                        findViewById(R.id.main_matrix_pos_10),
                        findViewById(R.id.main_matrix_pos_11),
                        findViewById(R.id.main_matrix_pos_12)
                },
                {
                        findViewById(R.id.main_matrix_pos_20),
                        findViewById(R.id.main_matrix_pos_21),
                        findViewById(R.id.main_matrix_pos_22)
                },
                {
                        findViewById(R.id.main_matrix_pos_30),
                        findViewById(R.id.main_matrix_pos_31),
                        findViewById(R.id.main_matrix_pos_32)
                },
                {
                        findViewById(R.id.main_matrix_pos_40),
                        findViewById(R.id.main_matrix_pos_41),
                        findViewById(R.id.main_matrix_pos_42)
                },
                {
                        findViewById(R.id.main_matrix_pos_50),
                        findViewById(R.id.main_matrix_pos_51),
                        findViewById(R.id.main_matrix_pos_52)
                },
                {
                        findViewById(R.id.main_matrix_pos_60),
                        findViewById(R.id.main_matrix_pos_61),
                        findViewById(R.id.main_matrix_pos_62)
                }
        };

        //array of cols cars
        main_cols_IMG_cars = new AppCompatImageView[]{
                findViewById(R.id.main_col_car_0),
                findViewById(R.id.main_col_car_1),
                findViewById(R.id.main_col_car_2)
        };

    }

    private void initViews() {
        main_FAB_left_arrow.setOnClickListener(v -> moveClicked("left"));
        main_FAB_right_arrow.setOnClickListener(v -> moveClicked("right"));

    }

    private void moveClicked(String direction) {
        gameManager.moveCar(direction);
        refreshUI();
    }

    private void refreshUI(){
        //lost
        if(gameManager.isGameLost()){
            Log.d("lost","lost");
            stopTimer();
            return;
        }
        //game on
        else{
            //move car part
            showCarUI();
            //update matrix
            updateMatrixUI();
            //update hearts
            updateHeartsUI();
        }
    };

    private void updateHeartsUI(){
        boolean crushNow = gameManager.checkCrushAndUpdateLivesAndNumCrushes();
        if (crushNow){
            toastAndVibrate("crush " + gameManager.getNumCrushes() +" !!!");
            main_IMG_hearts[gameManager.getNumCrushes() - 1].setVisibility(View.INVISIBLE);
        }

    }
    private void updateMatrixUI(){
        for (int i = 0; i < gameManager.getMatrixRows(); i++) {
            for (int j = 0; j < gameManager.getMatrixCols(); j++) {
                String currentCol = gameManager.getMatrix()[i][j];
                if(currentCol.equals(gameManager.getNONE())){
                    main_matrix_IMG_barriers[i][j].setVisibility(View.INVISIBLE);
                } else if (currentCol.equals(gameManager.getBARRIER())) {
                    main_matrix_IMG_barriers[i][j].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showCarUI(){
        int currentCarPos = gameManager.getCarPosition();
        Log.d("pos","" + currentCarPos);
        for (int i = 0; i < gameManager.getMatrixCols(); i++){
            if(i == currentCarPos){
                main_cols_IMG_cars[i].setVisibility(View.VISIBLE);
            }
            else{
                main_cols_IMG_cars[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void changeMatrixTime(){
        gameManager.matrixChangePeriod();
        refreshUI();
    }

    private void startTimer() {
        if (!timerOn) {
            Log.d("startTimer", "startTimer: Timer Started");
            startTime = System.currentTimeMillis();
            timerOn = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> changeMatrixTime());
                }
            },0L,DELAY);
        }
    }

    private void stopTimer() {
        timerOn = false;
        Log.d("stopTimer", "stopTimer: Timer Stopped");
        timer.cancel();
    }

    private void toastAndVibrate(String text) {
        vibrate();
        toast(text);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

}