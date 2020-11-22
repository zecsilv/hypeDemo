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

package com.hypelabs.demo.bundle.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hypelabs.demo.bundle.DemoDefs;
import com.hypelabs.demo.bundle.R;
import com.hypelabs.demo.bundle.database.ChatPersistence;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;
import com.hypelabs.hype.Instance;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment implements View.OnClickListener {

    private View view;
    private OnFragmentInteractionListener listener;
    private String destinataryContact;
    private String domesticContact;
    private ListView lvWindowMessages;
    private ImageButton ibSendImage;
    private ImageButton ibSendPushToTalk;
    private ImageButton ibSendFile;
    private ImageButton ibSendMessage;
    private TextView tvDestinataryNick;
    private TextView tvMyNick;
    private EditText etInputText;
    private MessageAdapter messageAdapter;
    private WeakReference<MessageDelegate> delegate;
    private static final int SELECT_PICTURE = 1;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    MediaRecorder recorder = new MediaRecorder();
    MediaPlayer mediaPlayer;


    private MessageDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    private MessageAdapter getMessageAdapter() {
        if (messageAdapter == null) {
//            messageAdapter = new MessageAdapter(getChatPersistence().getMessages(getDestinataryContact()), R.layout.fragment_message, getActivity(), getContext(), getDomesticContact());
            messageAdapter = new MessageAdapter(R.layout.fragment_message, getActivity(), getContext(), getDomesticContact(), getDestinataryContact());
        }
        return messageAdapter;
    }

    private String getDomesticContact() {
        return domesticContact;
    }

    private EditText getEtInputText() {
        if (etInputText == null) {
            etInputText = (EditText) getView().findViewById(R.id.etInputText);
        }
        return etInputText;
    }

    private ListView getLvWindowMessages() {
        if (lvWindowMessages == null) {
            lvWindowMessages = (ListView) getView().findViewById(R.id.lvWindowMessages);
        }
        return lvWindowMessages;
    }

    private ImageButton getIbSendMessage() {
        if (ibSendMessage == null) {
            ibSendMessage = (ImageButton) getView().findViewById(R.id.ibSendMessage);
        }
        return ibSendMessage;
    }

    private ImageButton getIbSendImage() {
        if (ibSendImage == null) {
            ibSendImage = (ImageButton) getView().findViewById(R.id.ibSendImage);
        }
        return ibSendImage;
    }

    private ImageButton getIbSendPushToTalk() {
        if (ibSendPushToTalk == null) {
            ibSendPushToTalk = (ImageButton) getView().findViewById(R.id.ibSendPushToTalk);
        }
        return ibSendPushToTalk;
    }

    private ImageButton getIbSendFile() {
        if (ibSendFile == null) {
            ibSendFile = (ImageButton) getView().findViewById(R.id.ibSendFile);
        }
        return ibSendFile;
    }

    private String getDestinataryContact() {
        return destinataryContact;
    }

    private TextView getTvDestinataryNick() {
        if (tvDestinataryNick == null) {
            tvDestinataryNick = (TextView) getView().findViewById(R.id.tvDestinataryNick);
        }
        return tvDestinataryNick;
    }

    private TextView getTvMyNick() {
        if (tvMyNick == null) {
            tvMyNick = (TextView) getView().findViewById(R.id.tvMyNick);
        }
        return tvMyNick;
    }

    public View getView() {
        return view;
    }

    public void setDelegate(MessageDelegate delegate) {
        this.delegate = new WeakReference<MessageDelegate>(delegate);
    }

    public void setDestinataryContact(String destinataryContact) {
        this.destinataryContact = destinataryContact;
    }

    public void setDomesticContact(String domesticContact) {
        this.domesticContact = domesticContact;
    }

    private void setView(View view) {
        this.view = view;
    }

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Trata ações dos botões de envio (texto/imagem/som/ficheiro).
     */
    public void addListenerOnButton() {
        // Codigo do botão envio de imagem
        getIbSendImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }

        });

        // Codigo do botão push-to-talk
        getIbSendPushToTalk().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.Log(LogLevel.LevelInfo, "Button DOWN...");
                    getIbSendPushToTalk().setVisibility(View.INVISIBLE);
                    try {
                        startRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.Log(LogLevel.LevelInfo, "Button UP...");
                    stopRecord();
                }
                return false;
            }
        });

        // Codigo do botão envio de ficheiro
        getIbSendFile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(getActivity(), "ibSendFile is clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Codigo do botão envio de menssagem de texto
        getIbSendMessage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (!(getEtInputText().getText().toString().equals(""))) {
                    Message message = new TextMessage(getEtInputText().getText().toString(), getDomesticContact(), getDestinataryContact());
                    getChatPersistence().addMessage(getDestinataryContact(), message);

                    createListViewMessages();
//                    updateListViewMessages();
                    getEtInputText().setText("");

                    try {
                        sendMessage(message, getContact(getDestinataryContact()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getActivity(), "Please, enter some characters", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContext().getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            Message message = new PhotoMessage(selectedImage, getDomesticContact(), getDestinataryContact());

            try {
                sendMessage(message, getContact(getDestinataryContact()));
                getChatPersistence().addMessage(getDestinataryContact(), message);
                createListViewMessages();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void startRecord() throws IOException {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "startRecord()");

        ParcelFileDescriptor[] descriptors = ParcelFileDescriptor.createPipe();
        ParcelFileDescriptor parcelRead = new ParcelFileDescriptor(descriptors[0]);
        ParcelFileDescriptor parcelWrite = new ParcelFileDescriptor(descriptors[1]);

        InputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelRead);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(parcelWrite.getFileDescriptor());

        recorder.prepare();

        recorder.start();

        int read;
        byte[] data = new byte[16384];

        while ((read = inputStream.read(data, 0, data.length)) != -1) {
            byteArrayOutputStream.write(data, 0, read);
        }

        byteArrayOutputStream.flush();

    }

    public void stopRecord() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "stopRecord()");
        recorder.stop();
        recorder.release();
        recorder = null;
    }


    public void playRecFile() throws IOException {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "PlayRecFile()");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource("OUT");
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    /**
     * Trata a informação recebida via Json e invoca o alarme, para o caso de falha envia um Log.
     *
     * @param message
     * @return
     */
    private JSONObject createJsonObj(Message message) {
        HashMap<String, String> auxArrayList = new HashMap<String, String>();
        auxArrayList.put(DemoDefs.IDENTIFIER_KEY, DemoDefs.getChatIdentifierString());
        auxArrayList.put(DemoDefs.MESSAGE_TYPE_KEY, Integer.toString(message.getMessageType()));

        if (message.getMessageType() == MessageType.TEXT.getValue()) {
            auxArrayList.put(DemoDefs.MESSAGE_CONTENT_KEY, ((TextMessage) message).getText());
        } else if (message.getMessageType() == MessageType.PHOTO.getValue()) {
            auxArrayList.put(DemoDefs.MESSAGE_CONTENT_KEY, Utils.encodeImage(((PhotoMessage) message).getImage()));
        } else {
            throw new RuntimeException("MessageType");
        }

        return new JSONObject(auxArrayList);
    }

    /**
     * Trata a informação recebida via Json, cria uma mensagem apartir dos dados recebidos e para o caso de falha despoleta uma excepção.
     *
     * @param json
     * @param instance
     */
    public void receiveData(JSONObject json, Instance instance) {
        int messageType;
        String content;
        try {
            messageType = Integer.parseInt(json.getString(DemoDefs.MESSAGE_TYPE_KEY));
            content = json.getString(DemoDefs.MESSAGE_CONTENT_KEY);
        } catch (Exception e) {
            Log.Log(LogLevel.LevelInfo, "ERROR");
            return;
        }

        if (content == null) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " null message");
            return;
        }

        Contact contact = null;
        try {
            contact = getContact(instance.getStringIdentifier());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (contact == null) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " contact doesnt exist");
            return;
        }

        Log.Log(LogLevel.LevelInfo, "DomesticContact " + getDomesticContact());

        Message m1 = null;
        if (messageType == MessageType.TEXT.getValue()) {
            m1 = new TextMessage(content, instance.getStringIdentifier(), getDomesticContact());
        } else if (messageType == MessageType.PHOTO.getValue()) {
            m1 = new PhotoMessage(Utils.decodeImage(content), instance.getStringIdentifier(), getDomesticContact());
        } else {
            throw new RuntimeException(" Message type not available ");
        }

        receiveMessage(contact, m1);
    }

    /**
     * Adiciona a mensagem recebida à persistencia e atualiza a IU.
     *
     * @param contact
     * @param message
     */
    private void receiveMessage(Contact contact, Message message) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " receiveMessage from contact = " + contact.getIdentifier());
        getChatPersistence().addMessage(contact.getIdentifier(), message);

        Log.Log(LogLevel.LevelInfo, "ANTES DO UPDATE");
//        updateListViewMessages();
        createListViewMessages();
        Log.Log(LogLevel.LevelInfo, "DEPOIS DO UPDATE");

    }

    /**
     * Cria uma listView para apresentar as mensagens com recurso a um adapter.
     */
    private void createListViewMessages() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " createListViewMessages");

        if (getView() == null) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "getView() == null on createListViewMessages method");
            return;
        }

        Handler mainHandler = new Handler(getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getLvWindowMessages().setAdapter(getMessageAdapter());
                Log.Log(LogLevel.LevelInfo,"Fez o setAdapter()");
            }
        };
        mainHandler.post(myRunnable);
    }


    /**
     * Atualiza a listView das mensagens.
     */
    private void updateListViewMessages() {
        Log.Log(LogLevel.LevelInfo, "Está no update ListView");

        if (getView() == null) {
            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "getView() == null");
            return;
        }

        final Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

//                getLvWindowMessages().invalidateViews();
//                getMessageAdapter().clear();

                getMessageAdapter().notifyDataSetChanged();
                getLvWindowMessages().invalidateViews();

                Log.Log(LogLevel.LevelInfo, "Fez update");
            }
        };
        mainHandler.post(myRunnable);
    }


    /**
     * Retorna um contacto da persistencia apartir de um identifier
     *
     * @param contactIdentifier
     * @return
     * @throws Exception
     */
    private Contact getContact(String contactIdentifier) throws Exception {
        return getChatPersistence().getContact(contactIdentifier);
    }

    /**
     * Acede à persistencia.
     *
     * @return
     */
    private ChatPersistence getChatPersistence() {
        return Persistence.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView(inflater.inflate(R.layout.fragment_message, container, false));
        getTvDestinataryNick().setText(getDestinataryContact());
        getTvMyNick().setText(getDomesticContact());
        addListenerOnButton();

        createListViewMessages();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (listener != null) {
            listener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.Log(LogLevel.LevelInfo, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.Log(LogLevel.LevelInfo, "Faz onDestroy() ???");
        getDelegate().notifyRemove(this, getDestinataryContact());
        super.onDetach();
        listener = null;
    }

    @Override
    public void onClick(View v) {
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
        void onFragmentInteraction(Uri uri);
    }

    private void sendMessage(Message message, Contact contact) {
        Log.Log(LogLevel.LevelInfo, "Entrou no sendMessage");
        try {
            getDelegate().onExchangeMessages(MessageFragment.this, createJsonObj(message), contact);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


}
