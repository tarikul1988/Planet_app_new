package planet.it.limited.planetapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import planet.it.limited.planetapp.R;
import planet.it.limited.planetapp.utill.BalanceTask;
import planet.it.limited.planetapp.utill.Constant;
import planet.it.limited.planetapp.utill.FontCustomization;
import planet.it.limited.planetapp.utill.LanguageUtility;

import static planet.it.limited.planetapp.utill.SaveValueSharedPreference.getValueFromSharedPreferences;
import static planet.it.limited.planetapp.utill.SaveValueSharedPreference.saveToSharedPreferences;
import static planet.it.limited.planetapp.utill.SaveValueSharedPreference.setValueToSharedPreferences;


public class SettingsActivity extends AppCompatActivity {
    AutoCompleteTextView edtUserName,edtPass,edtSenderNum;
    Button btnSave,btnReset;
    TextView txvUserName,txvPassword,txvSenderNum,txvToolbarText;
    Toolbar toolbar;
    FontCustomization fontCustomization;
    AlertDialog b;
    LanguageUtility languageUtility;
    BalanceTask balanceTask;
    public Constant constant;
    public static boolean checkBalance;

    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    RelativeLayout rlProgress;
    //boolean isCheckUserAndPassword;
    public static String userName = " ";
    public static String password = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar)findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });

       // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        initViews();
    }

    public void initViews(){

        balanceTask = new BalanceTask(SettingsActivity.this);
        constant = new Constant(SettingsActivity.this);
        languageUtility = new LanguageUtility(SettingsActivity.this);
        fontCustomization = new FontCustomization(SettingsActivity.this);
        txvToolbarText = (TextView)findViewById(R.id.txv_main);
        txvUserName = (TextView)findViewById(R.id.txv_username_head);
        txvPassword = (TextView)findViewById(R.id.txv_password_head);
        txvSenderNum = (TextView)findViewById(R.id.txv_from_sender);
        edtUserName = (AutoCompleteTextView) findViewById(R.id.txv_user_name);
        edtPass = (AutoCompleteTextView) findViewById(R.id.txv_password);
        edtSenderNum = (AutoCompleteTextView) findViewById(R.id.txv_sender);
        btnSave = (Button)findViewById(R.id.btn_save);
        btnReset = (Button)findViewById(R.id.btn_reset);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rlProgress = (RelativeLayout)findViewById(R.id.rl_progress);

        // to set font style
        txvToolbarText.setTypeface(fontCustomization.getHeadLandOne());
        txvUserName.setTypeface(fontCustomization.getTexgyreHerosRegular());
        txvPassword.setTypeface(fontCustomization.getTexgyreHerosRegular());
        txvSenderNum.setTypeface(fontCustomization.getTexgyreHerosRegular());
        edtUserName.setTypeface(fontCustomization.getTexgyreHerosRegular());
        edtPass.setTypeface(fontCustomization.getTexgyreHerosRegular());
        edtSenderNum.setTypeface(fontCustomization.getTexgyreHerosRegular());
        btnSave.setTypeface(fontCustomization.getTexgyreHerosRegular());
        btnReset.setTypeface(fontCustomization.getTexgyreHerosRegular());
        btnSave.setTransformationMethod(null);
        btnReset.setTransformationMethod(null);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  userName = edtUserName.getText().toString();
                  password = edtPass.getText().toString();

                saveToSharedPreferences("user_name",userName,SettingsActivity.this);
                saveToSharedPreferences("pass_word",password,SettingsActivity.this);
                saveToSharedPreferences("sender_number",edtSenderNum.getText().toString(),SettingsActivity.this);


                    if(userName.length()>0 && password.length()>0){
                        if(constant.isConnectingToInternet()){
                            rlProgress.setVisibility(View.VISIBLE);
                            startProgressBar();
                            balanceTask.getBalance(userName,password);
                        }

                    }else {
                        Toast.makeText(SettingsActivity.this, "User Name or Password empty,Must give user name and password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                    if(checkBalance){
                        String msgId = "You Info Save Success" ;
                        rlProgress.setVisibility(View.GONE);
                        openDialog(msgId,true);

                    }else {
                        String msgId = "User Name Or Password Not Valid ! please give valid user name and password";
                        rlProgress.setVisibility(View.GONE);
                        openDialog(msgId,false);

                    }

                    }
                },4000);// set time as per your requirement


            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtUserName.setText("");
                edtPass.setText("");
                edtSenderNum.setText("");
                saveToSharedPreferences("user_name","",SettingsActivity.this);
                saveToSharedPreferences("pass_word","",SettingsActivity.this);
                saveToSharedPreferences("sender_number","",SettingsActivity.this);
                saveToSharedPreferences("balance","",SettingsActivity.this);
                Toast.makeText(SettingsActivity.this,"You Info Reset Success",Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void startProgressBar(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus < 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                            txtProgress.setText(pStatus + " %");
                        }
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String senderNum =getValueFromSharedPreferences("sender_number",SettingsActivity.this);;
        String userName = getValueFromSharedPreferences("user_name",SettingsActivity.this); ;
        String password = getValueFromSharedPreferences("pass_word",SettingsActivity.this);;

        if(senderNum!=null){
            edtSenderNum.setText(senderNum);
        }
        if(password!=null){
            edtPass.setText(password);
        }
        if (userName!=null){
            edtUserName.setText(userName);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Sudip: 20170212
        switch (item.getItemId()) {

            case R.id.menu_language:
                showChangeLangDialog();
                break;


            default:
                return super.onOptionsItemSelected(item);
        }


        return super.onOptionsItemSelected(item);
    }

    public void showChangeLangDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dialog, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

//        dialogBuilder.setTitle(getResources().getString(R.string.lang_dialog_title));
        dialogBuilder.setMessage(getResources().getString(R.string.lang_dialog_message));
        dialogBuilder.setPositiveButton(R.string.alert_dialog_change_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch (langpos) {
                    case 1: //bengali

                        languageUtility.selectLanguage("bn");
                        setValueToSharedPreferences("language","bn",SettingsActivity.this);
                        restartApp();

                        return;
                    case 2: //english

                        languageUtility.selectLanguage("en");
                        setValueToSharedPreferences("language","en",SettingsActivity.this);
                        restartApp();

                        return;

                    default: //By default set to english

                        languageUtility.selectLanguage("en");
                        setValueToSharedPreferences("language","en",SettingsActivity.this);
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.alert_dialog_cancel_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        b = dialogBuilder.create();
        b.show();
    }

    public  void restartApp(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    public void openDialog(String msgId, final boolean isCheckUserAndPassword) {
        final Dialog dialog = new Dialog(SettingsActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.custom_dialog);
        TextView txvResponseMsg = (TextView) dialog.findViewById(R.id.dialog_info);
        txvResponseMsg.setText(msgId);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok);
//        Button cancleButton = (Button) dialog.findViewById(R.id.dialog_cancel);

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isCheckUserAndPassword){
                    ActivityCompat.finishAffinity(SettingsActivity.this);
                    Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                    startActivity(intent);
                }else {
                    ActivityCompat.finishAffinity(SettingsActivity.this);
                    Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
//        cancleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });

        dialog.show();
    }
}
