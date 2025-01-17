package com.nexis.kelimebilmeceoyunum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {
    // Sorular için Listeler
    private String[] sorularList={"Mutfakta iş yaparken veya yemek yerken kullanılan aletler nelerdir?","İç Anadolu Bölgesindeki İller?"};
    private String[] sorularKodList={"mutfakS1","illerS1"};

    //Kelimeler için listeler
    private String[] kelimelerList={"Çatal","Bıçak","Kaşık","Tabak","Bulaşık Süngeri","Bulaşık Teli","Tencere","Tava","Çaydanlık",
                       "Mutfak Robotu","Kesme Tahtası","Süzgeç",
                      "Aksaray","Ankara","Çankırı","Eskişehir","Karaman","Kayseri","Kırıkkale","Kırşehir","Konya",
                       "Nevşehir","Niğde","Sivas","Yozgat"};

    private String[] kelimelerKodList={"mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1",
            "mutfakS1","mutfakS1","mutfakS1",
            "illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1","illerS1"};


    private ProgressBar mProgress;
    private TextView mTextView;
    private SQLiteDatabase database;
    private Cursor cursor;
    private float maksimumProgress=100f,artacakProgress,progressMiktari=0;
    static public HashMap<String,String> sorularHashmap;
    private String sqlSorgusu;
    private SQLiteStatement statement;
    private MediaPlayer gameTheme;

    private SharedPreferences preferences;
    private boolean muzikDurumu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mProgress=(ProgressBar)findViewById(R.id.splash_screen_activity_progressBar);
        mTextView=(TextView)findViewById(R.id.splash_screen_activity_textViewState);
        sorularHashmap=new HashMap<>();
        gameTheme=MediaPlayer.create(this,R.raw.gametheme);
        gameTheme.setLooping(true);

        preferences =this.getSharedPreferences("com.nexis.kelimebilmeceoyunum",MODE_PRIVATE);
        muzikDurumu=preferences.getBoolean("muzikDurumu",true);

        try{
             database=this.openOrCreateDatabase("KelimeBilmece",MODE_PRIVATE,null);
             database.execSQL("CREATE TABLE IF NOT EXISTS Ayarlar (k_adi VARCHAR, k_heart VARCHAR, k_image BLOB)");
             cursor=database.rawQuery("SELECT * FROM Ayarlar",null);

             if (cursor.getCount()<1)
                 database.execSQL("INSERT INTO Ayarlar (k_adi,k_heart) VALUES ('Oyuncu','0')");





            database.execSQL("CREATE TABLE IF NOT EXISTS Sorular (id INTEGER PRIMARY KEY, sKod VARCHAR UNIQUE, soru VARCHAR)");
            database.execSQL("DELETE FROM Sorular");
            sqlSorulariEkle();

            database.execSQL("CREATE TABLE IF NOT EXISTS Kelimeler (kKod VARCHAR, kelime VARCHAR, FOREIGN KEY (kKod) REFERENCES Sorular (sKod))");
            database.execSQL("DELETE FROM Kelimeler");
            sqlKelimeleriEkle();

            cursor=database.rawQuery("SELECT * FROM Sorular",null);
            artacakProgress=maksimumProgress / cursor.getCount();

            int sKodIndex=cursor.getColumnIndex("sKod");
            int soruIndex=cursor.getColumnIndex("soru");

            mTextView.setText("Sorular Yükleniyor...");

            while (cursor.moveToNext()){
                sorularHashmap.put(cursor.getString(sKodIndex), cursor.getString(soruIndex));
                progressMiktari+=artacakProgress;
                mProgress.setProgress((int)progressMiktari);
            }

            mTextView.setText("Sorular Alındı, Uygulama Başlatılıyor...");
            cursor.close();

            new CountDownTimer(1100,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent mainIntent=new Intent(SplashScreenActivity.this,MainActivity.class);
                    startActivity(mainIntent);

                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (muzikDurumu)
            gameTheme.start();

    }

    private void sqlSorulariEkle(){
       try {
           for (int s=0; s<sorularList.length;s++){
               sqlSorgusu="INSERT INTO Sorular(sKod,soru) VALUES (?,?) ";
               statement=database.compileStatement(sqlSorgusu);
               statement.bindString(1,sorularKodList[s]);
               statement.bindString(2,sorularList[s]);
               statement.execute();

           }
       } catch (Exception e){
           e.printStackTrace();
       }

    }
    private void sqlKelimeleriEkle(){
        try {
            for (int k=0;k<kelimelerList.length;k++)
            {
                sqlSorgusu="INSERT INTO Kelimeler(kKod,kelime) VALUES (?,?)";
                statement=database.compileStatement(sqlSorgusu);
                statement.bindString(1,kelimelerKodList[k]);
                statement.bindString(2,kelimelerList[k]);
                statement.execute();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}