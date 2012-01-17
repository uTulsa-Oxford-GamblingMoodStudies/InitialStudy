package edu.utulsa.ibcb.moodstudy;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.view.WindowManager;

public class DevelopmentPreferencesActivity extends PreferenceActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        addPreferencesFromResource(R.xml.development_preferences);
    }
}
