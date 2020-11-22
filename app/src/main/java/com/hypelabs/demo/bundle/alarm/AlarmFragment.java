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

package com.hypelabs.demo.bundle.alarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hypelabs.demo.bundle.DemoDefs;
import com.hypelabs.demo.bundle.database.AlarmPersistence;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;
import com.hypelabs.demo.bundle.R;
import com.hypelabs.hype.Instance;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AlarmFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private ToggleButton btnAlarm;
    private View view;
    private TextView tvNumInstancesFound;
    private TextView tvNumAlarms;

    private OnFragmentInteractionListener listener;
    private WeakReference<AlarmDelegate> delegate;
    private MediaPlayer mediaPlayer;
    private int alarmCount = 0;

    private HashMap<String, Instance> instanceFound;
    private HashMap<String, Instance> instancesStartedAlarms;
    private static final String ALARM_STATE_RUNNING = "1";
    private static final String ALARM_STATE_IDLE = "0";
    private Boolean isAlarmRunning = false;

    public AlarmFragment() {
    }

    private int getAlarmCount() {
        return alarmCount;
    }

    private void setAlarmCount(int alarmCount) {
        if (alarmCount != this.alarmCount) {
            this.alarmCount = alarmCount;
        }
    }

    private int incAlarmCount() {
        setAlarmCount(getAlarmCount() + 1);

        return getAlarmCount();
    }

    private int decAlarmCount() {
        if (getAlarmCount() == 0) {
            throw new RuntimeException("Alarm reached a negative count");
        }

        setAlarmCount(getAlarmCount() - 1);

        return getAlarmCount();
    }

    public void setDelegate(AlarmDelegate delegate) {
        this.delegate = new WeakReference<AlarmDelegate>(delegate);
    }

    private ToggleButton getBtnAlarm() {
        return btnAlarm;
    }

    private void setButtonAlarm(ToggleButton buttonAlarm) {
        this.btnAlarm = buttonAlarm;
    }

    /**
     * Acesso à TextView onde é apresentado o contador de instâncias encontradas.
     *
     * @return TextView onde é apresentado o contador de instâncias encontradas.
     */
    private TextView getTvNumInstancesFound() {
        if (tvNumInstancesFound == null) {
            tvNumInstancesFound = (TextView) getView().findViewById(R.id.tvNumInstancesFound);
        }
        return tvNumInstancesFound;
    }

    /**
     * Acesso à TextView onde é apresentado o contador de alarmes mandados executar.
     *
     * @return
     */
    private TextView getTvNumAlarms() {
        if (tvNumAlarms == null) {
            tvNumAlarms = (TextView) getView().findViewById(R.id.tvNumAlarms);
        }
        return tvNumAlarms;
    }

    /**
     * HashMap com as instâncias que executaram alarmes.
     *
     * @return um HashMap com as instâncias que executram alarmes.
     */
    private HashMap<String, Instance> getInstancesStartedAlarms() {
        if (instancesStartedAlarms == null) {
            instancesStartedAlarms = new HashMap<String, Instance>();
        }
        return instancesStartedAlarms;
    }

    /**
     * HashMap com as instances que foram encontradas (found).
     *
     * @return HashMap com as instances que foram encontradas.
     */
    private HashMap<String, Instance> getInstanceFound() {
        if (instanceFound == null) {
            instanceFound = new HashMap<String, Instance>();
        }
        return instanceFound;
    }

    @Nullable
    private AlarmDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    private AlarmPersistence getAlarmPersistence() {
        return Persistence.getInstance();
    }

    /**
     * Cria um media player que permite executar um som.
     *
     * @return um media player que permite executar um som.
     */
    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm_sound_1);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        }
        return mediaPlayer;
    }

    private Boolean isAlarmRunning() {
        return isAlarmRunning;
    }

    private void setAlarmRunning(Boolean alarmRunning) {
        isAlarmRunning = alarmRunning;
    }

    /**
     * Remove a instância do HashMap,verifica se a mesma
     * despoletou um alarme, em caso positivo, para a execução desse alarme.
     *
     * @param instance
     */
    public void removeInstance(Instance instance) {

        getInstanceFound().remove(instance.getStringIdentifier());
        updateNearbyPeers();

        Instance inst = getInstancesStartedAlarms().get(instance.getStringIdentifier());

        if (inst != null) {
            receiveStop(inst);
        }
    }

    /**
     * Adiciona a instância encontrada a um HashMap e actualiza o contador de instâncias encontradas.
     *
     * @param instance
     */
    public void addInstance(Instance instance) {
        getInstanceFound().put(instance.getStringIdentifier(), instance);
        updateNearbyPeers();
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Responsável pela inicialização da IU.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView(inflater.inflate(R.layout.fragment_alarm, container, false));
        updateNearbyPeers();
        updateNumAlarmStarted();
        setButtonAlarm((ToggleButton) getView().findViewById(R.id.id_btn_alarm));
        getBtnAlarm().setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (getInstanceFound().size() == 0) {
            Toast.makeText(getActivity(), "Sorry, no equipment found in nearby!", Toast.LENGTH_SHORT).show();
            getBtnAlarm().setChecked(false);

        } else {
            if (getBtnAlarm().isChecked()) {
                Toast.makeText(getActivity(), "Sending Alert...", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "Alert Stopped!", Toast.LENGTH_SHORT).show();
            }

            getDelegate().onChangeState(this, createJsonObj());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " onCompletion");
    }

    /**
     * Método que prepara o media player para ser executado, e apos isso inicia a sua execução.
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " onPrepared");
        if (getInstancesStartedAlarms().size() > 0) {
            startMediaPlayer();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
    }

    /**
     * Cria objeto Json tendo como validação se o alarme está em execução ou não.
     *
     * @return Objecto JSon com um identificador e o estado do alarme.
     */
    private JSONObject createJsonObj() {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(DemoDefs.IDENTIFIER_KEY, DemoDefs.getAlarmIdentifierString());

        if (!isAlarmRunning()) {
            setAlarmRunning(true);
            map.put(DemoDefs.ALARM_SOUND_KEY, ALARM_STATE_RUNNING);
        } else {
            setAlarmRunning(false);
            map.put(DemoDefs.ALARM_SOUND_KEY, ALARM_STATE_IDLE);
        }

        return new JSONObject(map);
    }

    /**
     * Trata a informação recebida via Json e invoca o alarme, para o caso de falha envia um Log.
     *
     * @param json
     * @param instance
     */
    public void receiveData(JSONObject json, Instance instance) {
        String aux;
        try {
            aux = json.getString(DemoDefs.ALARM_SOUND_KEY);
        } catch (JSONException e) {
            Log.Log(LogLevel.LevelInfo, "ERROR");
            return;
        }

        if (aux.equalsIgnoreCase(ALARM_STATE_RUNNING)) {
            receiveStart(instance);
        } else if (aux.equalsIgnoreCase(ALARM_STATE_IDLE)) {
            receiveStop(instance);
        } else {
            throw new RuntimeException("Unexpected alarm value");
        }
    }

    /**
     * Incrementa o contador de alarmes e actualiza o número de alarmes em execução e adiciona a instância ao HashMap de alarmes iniciados colocando o som em execução.
     *
     * @param instance
     */
    private void receiveStart(Instance instance) {
        if (getInstancesStartedAlarms().get(instance.getStringIdentifier()) == null) {
            if (getInstancesStartedAlarms().size() == 0) {
                startSound();
            }

            incAlarmCount();
            updateNumAlarmStarted();
            getInstancesStartedAlarms().put(instance.getStringIdentifier(), instance);
        }
    }

    /**
     * Método que para o som a executar, decrementa o contador de alarmes e atualiza o numero de alarmes em execução.
     *
     * @param instance
     */
    private void receiveStop(Instance instance) {
        Instance inst = getInstancesStartedAlarms().remove(instance.getStringIdentifier());

        if (inst != null) {
            decAlarmCount();
            updateNumAlarmStarted();

            if (getInstancesStartedAlarms().size() == 0) {
                stopSound();
            }
        }
    }

    /**
     * Para o som em execução
     */
    private void stopSound() {
        try {
            if (getMediaPlayer().isPlaying()) {
                getMediaPlayer().setLooping(false);
                getMediaPlayer().stop();
            } else {
                Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " Ignoring. Sound already stopped.");
            }
        } catch (IllegalStateException e) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " IllegalStateException = " + e.toString());
        }
    }

    /**
     * Coloca o som em execução
     */
    private void startSound() {
        try {
            if (!getMediaPlayer().isPlaying()) {
                getMediaPlayer().setLooping(true);
                getMediaPlayer().prepareAsync();

            } else {
                Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " ignoring. Sound already starded.");
            }
        } catch (IllegalStateException e) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " startMediaPlayer prepareAsync exception = " + e.toString());
            startMediaPlayer();
        }
    }

    /**
     * Executa o Media Player
     */
    private void startMediaPlayer() {
        try {
            getMediaPlayer().start();
        } catch (IllegalStateException e1) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " startMediaPlayer IllegalStateException = " + e1.toString());
        }
    }

    /**
     * Actualiza o contador de instâncias encontradas.
     */
    private void updateNearbyPeers() {
        if (getView() == null) {
            return;
        }

        Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getTvNumInstancesFound().setText("" + getInstanceFound().size());
            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * Actualiza o contador de alarmes que foram colocados executar
     */
    private void updateNumAlarmStarted() {
        if (getView() == null) {
            return;
        }

        Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getTvNumAlarms().setText("" + getInstancesStartedAlarms().size());
            }
        };
        mainHandler.post(myRunnable);
    }

}