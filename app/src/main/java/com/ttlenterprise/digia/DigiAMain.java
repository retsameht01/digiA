package com.ttlenterprise.digia;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.speech.tts.TextToSpeech;

public class DigiAMain extends AppCompatActivity {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 11001;
    Button startBtn;
    TextView msgText;
    TextToSpeech ttsObject;
    String[] jokes;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digi_amain);
        initTextToSpeechObject();

        jokes = getResources().getStringArray(R.array.dirty_jokes);
        startBtn = (Button) findViewById(R.id.startBtn);
        msgText = (TextView) findViewById(R.id.txtView);
        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                speak();
                //Toast.makeText(view.getContext(), "Start button click", Toast.LENGTH_LONG).show();
            }
        });
        //check voice recognition
        checkVoiceRecognition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void initTextToSpeechObject(){

        ttsObject = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsObject.setLanguage(Locale.US);
            }
        });
    }

    //launch the speech recognition activity
    private void checkVoiceRecognition(){
        PackageManager pMgr= getPackageManager();
        List<ResolveInfo> activities = pMgr.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if(activities.size() == 0){
            startBtn.setEnabled(false);
            showToastMessage("Speech recognition not supported!!");
        }
    }


    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


            if(!textMatchList.isEmpty()){
                //showToastMessage(textMatchList.get(0));
                processVoiceQuery(textMatchList.get(0));


                /*

                if(textMatchList.get(0).contains("search")){
                    String searchQuery = textMatchList.get(0);
                    searchQuery = searchQuery.replace("search","");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);

                }else{
                    //do something here

                }

                */
            }

        }else{
            if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }

            speakMsg("Sorry, I don't understand.");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    //This will be the entry point to the natural language processing algorithm

    private void processVoiceQuery(String query){
        msgText.setText(query);
        if(query.contains("joke")){
            Random rand = new Random();
            int index = rand.nextInt(jokes.length);
            speakMsg(jokes[index]);
        }else if(query.contains("search")){
            String searchString = query.substring(query.indexOf("search")+6);
            startWebSearch(searchString);
        }
        else{
            speakMsg("You said "+query);
        }
    }

    private void startWebSearch(String searchString){
        try{
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY,searchString);
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    private void speakMsg(String msg){
        ttsObject.speak(msg,TextToSpeech.QUEUE_FLUSH,null);
    }


    private void showToastMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_digi_amain, menu);
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DigiAMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.ttlenterprise.digia/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DigiAMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.ttlenterprise.digia/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
