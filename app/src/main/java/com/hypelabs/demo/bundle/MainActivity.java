/*
 * Copyright 2017 Hype Labs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hypelabs.demo.bundle;

import android.Manifest;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hypelabs.demo.bundle.alarm.AlarmDelegate;
import com.hypelabs.demo.bundle.alarm.AlarmFragment;
import com.hypelabs.demo.bundle.chat.ContactFragment;
import com.hypelabs.demo.bundle.chat.ChatManager;
import com.hypelabs.demo.bundle.chat.ChatManagerDelegate;
import com.hypelabs.demo.bundle.chat.MessageFragment;
import com.hypelabs.demo.bundle.database.InstancePersistence;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.metrics.MetricsFragment;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;
import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;
import com.hypelabs.hype.NetworkObserver;
import com.hypelabs.hype.StateObserver;
import com.hypelabs.hype.TransportType;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MessageFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener, AlarmFragment.OnFragmentInteractionListener, ContactFragment.OnFragmentInteractionListener,
        MetricsFragment.OnFragmentInteractionListener, StateObserver, NetworkObserver, MessageObserver, AlarmDelegate, ChatManagerDelegate {

    private final static String REALM = "f401782b";
    private AlarmFragment alarmFragment;
    private MetricsFragment metricsFragment;
    private HomeFragment homeFragment;
    private ChatManager chatManager;

    private HomeFragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    private AlarmFragment getAlarmFragment() {
        if (alarmFragment == null) {
            alarmFragment = new AlarmFragment();
        }
        return alarmFragment;
    }

    private ChatManager getChatManager() {
        if (chatManager == null) {
            chatManager = new ChatManager(this);
        }
        return chatManager;
    }

    private MetricsFragment getMetricsFragment() {
        if (metricsFragment == null) {
            metricsFragment = new MetricsFragment();
        }
        return metricsFragment;
    }

    public InstancePersistence getPersistence() {
        return Persistence.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Hype.getInstance().addMessageObserver(this);
        Hype.getInstance().addNetworkObserver(this);
        Hype.getInstance().addStateObserver(this);

        Hype.getInstance().setContext(getApplicationContext());
        Hype.getInstance().setOption(Hype.OptionRealmKey, REALM);
//        Hype.getInstance().setOption(Hype.OptionTransportKey, TransportType.BLUETOOTH_LOW_ENERGY);

        Hype.getInstance().start();

    }

    @Override
    public void onBackPressed() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " onBackPressed - getBackStackEntryCount = " + getFragmentManager().getBackStackEntryCount());
//
//        int a = getFragmentManager().getBackStackEntryCount();
//        Log.Log(LogLevel.LevelInfo, "a = " + a );
//        if(getFragmentManager().getBackStackEntryAt(a).getName().equalsIgnoreCase(DemoDefs.CONTACT_TAG))
//            Log.Log(LogLevel.LevelInfo, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//


//        int index = getFragmentManager().getBackStackEntryCount() - 1;
//
//        Log.Log(LogLevel.LevelInfo, "index:" + index);
//        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(index);
//        String tag = backEntry.getName();
//        Log.Log(LogLevel.LevelInfo, "tag:" + tag);


//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            Log.Log(LogLevel.LevelInfo, "Entrou dentro do >0");
//            getFragmentManager().popBackStack();
//        } else {
        Log.Log(LogLevel.LevelInfo, "Super.onBackPressed()");
        super.onBackPressed();
//            Log.Log(LogLevel.LevelInfo,"Size MessageFragment: " + getChatManager().getMessageFragments().size());
//            Log.Log(LogLevel.LevelInfo,"Size MessageFragment: " + getChatManager().getMessageFragments().size());

        getChatManager().removeContactFragment();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void initFragment(Fragment frag, String tagIdentifier) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.id_container, frag, tagIdentifier);
        transaction.addToBackStack(tagIdentifier);
        transaction.commit();

        /*getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.Log(LogLevel.LevelInfo, " MainActivity onBackStackChanged");
                if (getSupportFragmentManager().getBackStackEntryCount()>1)
                {
                   getSupportFragmentManager().popBackStack();
                }
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.Log(LogLevel.LevelInfo, "onFragmentInteraction");
    }

    //message observer
    @Override
    public void onMessageReceived(Hype hype, Message message, Instance instance) {
        String str = new String(message.getData(), StandardCharsets.UTF_8);

        try {
            JSONObject json = new JSONObject(str);

            callFragment(json, instance);
        } catch (JSONException e) {
            Log.Log(LogLevel.LevelInfo, "Error in convertion @ " + str);
        }
    }

    /**
     * Inicia os fragments referentes a cada menu.
     *
     * @param json
     * @param instance
     */
    private void callFragment(JSONObject json, Instance instance) {
        try {
            int id = Integer.parseInt(json.getString(DemoDefs.IDENTIFIER_KEY));

            switch (id) {
                case DemoDefs.ALARM_IDENTIFIER:
                    //call alarm fragment
                    getAlarmFragment().receiveData(json, instance);
                    break;

                case DemoDefs.CHAT_IDENTIFIER:
                    //call chat fragment
                    getChatManager().receiveData(json, instance);

                    break;

                case DemoDefs.METRICS_IDENTIFIER:
                    //call metrics fragment
                    getMetricsFragment().receiveData(json);
                    break;

                default:
                    Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " case default");
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment frag = null;

        if (id == R.id.nav_home) {
            frag = new HomeFragment();
            initFragment(frag, DemoDefs.HOME_TAG);
        } else if (id == R.id.nav_alarm) {
            getAlarmFragment().setDelegate(this);
            initFragment(getAlarmFragment(), DemoDefs.ALARM_TAG);
        } else if (id == R.id.nav_chat) {
            initFragment(getChatManager().getFragment(), DemoDefs.CONTACT_TAG);
        } else if (id == R.id.nav_metrics) {
            frag = new MetricsFragment();
            initFragment(frag, DemoDefs.METRIC_TAG);
        }
        return true;
    }

    @Override
    public void onMessageFailedSending(Hype hype, MessageInfo messageInfo, Instance instance, Error error) {
        Log.Log(LogLevel.LevelInfo, "onMessageFailedSending");
    }

    @Override
    public void onMessageSent(Hype hype, MessageInfo messageInfo, Instance instance, float v, boolean b) {
        Log.Log(LogLevel.LevelInfo, "onMessageSent");
    }

    @Override
    public void onMessageDelivered(Hype hype, MessageInfo messageInfo, Instance instance, float v, boolean b) {
        Log.Log(LogLevel.LevelInfo, "onMessageDelivered");
    }

    //network observer

    /**
     * Adiciona a instancia encontrada a um HashMap de instancias
     *
     * @param hype
     * @param instance
     */
    @Override
    public void onInstanceFound(Hype hype, Instance instance) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " --- onInstanceFound ---");

        addInstance(instance);
    }

    public void addInstance(Instance instance) {
        getPersistence().addInstance(instance);
        getAlarmFragment().addInstance(instance);
        Log.Log(LogLevel.LevelInfo, "NOTIFY INSTANCE FOUND");
        getChatManager().notifyFoundInstance(instance);
    }

    /**
     * Remove uma instancia.
     *
     * @param hype
     * @param instance
     * @param error
     */
    @Override
    public void onInstanceLost(Hype hype, Instance instance, Error error) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " onInstanceLost");

        removeInstance(instance);
    }

    private void removeInstance(Instance instance) {
        getPersistence().removeInstance(instance.getStringIdentifier());
        getAlarmFragment().removeInstance(instance);
        getChatManager().notifyLostInstance(instance);
    }

    //state observer
    @Override
    public void onStart(Hype hype) {
        Log.Log(LogLevel.LevelInfo, "onStart");
    }

    @Override
    public void onStop(Hype hype, Error error) {
        Log.Log(LogLevel.LevelInfo, "onStop");
    }

    @Override
    public void onFailedStarting(Hype hype, Error error) {
        Log.Log(LogLevel.LevelInfo, "onFailedStarting");
    }

    @Override
    public void onReady(Hype hype) {
        Log.Log(LogLevel.LevelInfo, "onReady");
        Hype.getInstance().start();
    }

    @Override
    public void onStateChange(Hype hype) {
        Log.Log(LogLevel.LevelInfo, "onStateChange");
    }

    @Override
    public void onChangeState(AlarmFragment alarmFragment, JSONObject json) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " onChangeState");

        for (Map.Entry<String, Instance> entry : getPersistence().getInstances().entrySet()) {
            String key = entry.getKey();
            Instance value = entry.getValue();

            Message m = Hype.getInstance().send(json.toString().getBytes(StandardCharsets.UTF_8), value, false);
        }
    }

    @Override
    public void onMessageReceived(ChatManager chatManager, JSONObject json) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "onMessageReceived");

        for (Map.Entry<String, Instance> entry : getPersistence().getInstances().entrySet()) {
            String key = entry.getKey();
            Instance value = entry.getValue();

            Message m = Hype.getInstance().send(json.toString().getBytes(StandardCharsets.UTF_8), value, false);
        }
    }

    /**
     * Devolve a Domestic Instance (fornecida pela framework Hype).
     *
     * @param chatManager
     * @return
     */
    @Override
    public String getNameDomesticInstance(ChatManager chatManager) {
        return Hype.getInstance().getDomesticInstance().getStringIdentifier();
    }

    @Override
    public Message onExchangeMessages(ChatManager chatManager, JSONObject json, Instance instance) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " onExchangeMessages");

        return Hype.getInstance().send(json.toString().getBytes(StandardCharsets.UTF_8), instance, false);
    }


}