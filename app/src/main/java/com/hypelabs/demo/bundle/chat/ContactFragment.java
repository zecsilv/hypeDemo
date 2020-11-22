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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hypelabs.demo.bundle.R;
import com.hypelabs.demo.bundle.database.ChatPersistence;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;
import com.hypelabs.hype.Instance;


import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment implements View.OnClickListener, AdapterDelegate {

    private View view;
    private TextView tvSize;
    private TextView tvMyNick;
    private WeakReference<ContactDelegate> delegate;
    private ListView lvContactsOnline;
    private ContactAdapter contactAdapter;
    private TextView tvCounterNewMessages;

    public ContactAdapter getContactAdapter() {
        if (contactAdapter == null) {
            contactAdapter = new ContactAdapter(getChatPersistence().getContacts(), getActivity(), getContext(), this);
        }
        return contactAdapter;
    }

    public TextView getTvCounterNewMessages() {
        if (tvCounterNewMessages == null) {
            tvCounterNewMessages = (TextView) getView().findViewById(R.id.tvCounterNewMessages);
        }
        return tvCounterNewMessages;
    }

    public TextView getTvMyNick() {
        if (tvMyNick == null) {
            tvMyNick = (TextView) getView().findViewById(R.id.tvMyNick);
        }
        return tvMyNick;
    }

    private ContactDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public TextView getTvSize() {
        if (tvSize == null) {
            tvSize = (TextView) getView().findViewById(R.id.tvSize);
        }
        return tvSize;
    }

    private ListView getLvContactsOnline() {
        if (lvContactsOnline == null) {
            lvContactsOnline = (ListView) getView().findViewById(R.id.lvContactsOnline);
        }
        return lvContactsOnline;
    }

    /**
     * Acede à persistencia
     *
     * @return
     */
    private ChatPersistence getChatPersistence() {
        return Persistence.getInstance();
    }

    @Override
    public View getView() {
        return view;
    }

    public void setDelegate(ContactDelegate delegate) {
        this.delegate = new WeakReference<ContactDelegate>(delegate);
    }

    public void setView(View view) {
        this.view = view;
    }

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.Log(LogLevel.LevelInfo, "-> onCreateView <-  = " + getChatPersistence().getContacts().size());
        setView(inflater.inflate(R.layout.fragment_chat, container, false));

        updateDomestic();

        updateNumberOfInstanceFound();
        createListViewContactsOnline();

        return view;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * Cria um contacto apartir de uma instância, e actualiza a UI.
     *
     * @param instance
     */
    public void notifyFoundInstance(Instance instance) {
        createContact(instance);
        Log.Log(LogLevel.LevelInfo, "ADICIONADA A INSTANCE _ Tamanho array:  " + getChatPersistence().getContacts().size());

        updateNumberOfInstanceFound();
        updateListViewContactsOnline();
    }

    /**
     * Remove o contatacto da persistência, e actualiza a UI.
     *
     * @param instance
     */
    public void notifyLostInstance(Instance instance) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + "nofityLostInstance");
        getChatPersistence().removeContact(instance.getStringIdentifier());

        updateNumberOfInstanceFound();
        updateListViewContactsOnline();
    }

    /**
     * Cria um contacto apartir de uma instancia e adiciona-o à persistência.
     *
     * @param instance
     */
    private void createContact(Instance instance) {
        Contact contact = new Contact(instance);
        getChatPersistence().addContact(contact);
    }

    private void updateDomestic() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " updateDomestic");
        Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getTvMyNick().setText(getDelegate().getNameDomesticInstance(ContactFragment.this));
            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * Actualiza o contador de instâncias encontradas (UI).
     */
    private void updateNumberOfInstanceFound() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " updateNumberOfInstanceFound " + getChatPersistence().getContacts().size());
        if (getView() == null) {
            return;
        }

        Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getTvSize().setText("" + getChatPersistence().getContacts().size());
            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * Notifica o delegate que foi selecionado um contacto, e solicita que seja invocado o fragment "messageFragment" desse contacto.
     *
     * @param contactAdapter
     * @param contact
     */
    @Override
    public void onSelectedContact(ContactAdapter contactAdapter, Contact contact) {
        getDelegate().callMessageFragment(this, contact, getActivity());
    }
//
//    public void updateCounterNewMessages(int position, String contactIdentifier) {
//        int count = 0;
//        getTvCounterNewMessages().setVisibility(View.VISIBLE);
//        getTvCounterNewMessages().setText(""+getChatPersistence().getMessages(contactIdentifier).size());
//        Log.Log(LogLevel.LevelInfo, "chega ao delegate");
//        Log.Log(LogLevel.LevelInfo, "SIZE menssagens" + getChatPersistence().getMessages(contactIdentifier).size());
//        for (int a = 0; a < getChatPersistence().getMessages(contactIdentifier).size(); a++) {
//
//            Log.Log(LogLevel.LevelInfo, "getChatPersistence().getContacts().get(position).getIdentifier() " + getChatPersistence().getContacts().get(position).getIdentifier());
//            Log.Log(LogLevel.LevelInfo, "getChatPersistence().getMessages().get(a).getOriginContactIdentifier()) igual ao anterior? :" + getChatPersistence().getMessages(contactIdentifier).get(a).getOriginContactIdentifier());
//            Log.Log(LogLevel.LevelInfo, "!getChatPersistence().getMessages().get(a).isRead() é false ?" + !getChatPersistence().getMessages(contactIdentifier).get(a).isRead());
//
//            if ((getChatPersistence().getContacts().get(position).getIdentifier().equalsIgnoreCase(getChatPersistence().getMessages(contactIdentifier).get(a).getOriginContactIdentifier()))) {
//                if (getChatPersistence().getMessages(contactIdentifier).get(a).isRead()) {
//                    count++;
//                    Log.Log(LogLevel.LevelInfo, "LOG counter : " + count);
//                }
//            }
//        }
//
//
//        if (count != 0 && count < 11) {
//            getTvCounterNewMessages().setVisibility(View.VISIBLE);
//            getTvCounterNewMessages().setText("" + count);
//        } else if (count != 0 && count > 10) {
//            getTvCounterNewMessages().setVisibility(View.VISIBLE);
//            getTvCounterNewMessages().setText("10+");
//        }
//    }

    public void updateNewMessages(Contact c) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " updateNewMessages");

        Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getLvContactsOnline().invalidateViews();

            }
        };
        mainHandler.post(myRunnable);


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

    /**
     * Atualiza a listView dos contactos online.
     */
    private void updateListViewContactsOnline() {
        if (getView() == null) {
            return;
        }

        final Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
//                getContactAdapter().notifyDataSetChanged();
                getLvContactsOnline().invalidateViews();

            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * Cria uma listView para apresentar os contactos com recurso a um adapter.
     */
    private void createListViewContactsOnline() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " createListViewContactsOnline adapter count: " + getContactAdapter().getCount());

        if (getView() == null) {
            return;
        }

        Handler mainHandler = new Handler(getContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getLvContactsOnline().setAdapter(getContactAdapter());

            }
        };
        mainHandler.post(myRunnable);
    }

}
