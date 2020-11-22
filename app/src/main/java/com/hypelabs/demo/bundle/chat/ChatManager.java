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

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.hypelabs.demo.bundle.DemoDefs;
import com.hypelabs.demo.bundle.R;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;
import com.hypelabs.hype.*;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class ChatManager implements ContactDelegate, MessageDelegate {

    private ContactFragment contactFragment;
    private HashMap<String, MessageFragment> messageFragments;
    private WeakReference<ChatManagerDelegate> delegate;

    public ChatManager(ChatManagerDelegate delegate) {
        this.delegate = new WeakReference<ChatManagerDelegate>(delegate);
    }

    private ContactFragment getContactFragment() {
        if (contactFragment == null) {
            contactFragment = new ContactFragment();
            contactFragment.setDelegate(this);
        }
        return contactFragment;
    }

    public void removeContactFragment() {
        contactFragment = null;
    }

    public HashMap<String, MessageFragment> getMessageFragments() {
        if (messageFragments == null) {
            messageFragments = new HashMap<String, MessageFragment>();
        }
        return messageFragments;
    }

    private MessageFragment getMessageFragment(String contactIdentifier) {
        MessageFragment m = getMessageFragments().get(contactIdentifier);

        if (m == null) {

            Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " m == null");

            MessageFragment messageFragment = new MessageFragment();
            messageFragment.setDelegate(this);
            messageFragment.setDomesticContact(getDelegate().getNameDomesticInstance(this));
            getMessageFragments().put(contactIdentifier, messageFragment);
            return messageFragment;
        } else {

            return m;
        }
    }

    /**
     * Devolve a Domestic Instance (fornecida pela framework Hype).
     *
     * @param contactFragment
     * @return
     */
    @Override
    public String getNameDomesticInstance(ContactFragment contactFragment) {
        return getDelegate().getNameDomesticInstance(this);
    }

    private ChatManagerDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public Fragment getFragment() {
        return getContactFragment();
    }

    public void receiveData(JSONObject jsonObject, Instance instance) {
        try {
            getContactFragment().updateNewMessages(Persistence.getInstance().getContact(instance.getStringIdentifier()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        getMessageFragment(instance.getStringIdentifier()).receiveData(jsonObject, instance);
    }

    /**
     * Invoca o delegate que notifica a mainActivity que foi enviada/recebida uma mensagem, passando o objeto JSON(conteudo menssagem) e o contacto que a emitiu.
     *
     * @param messageFragment
     * @param json
     * @param contact
     */
    @Override
    public void onExchangeMessages(MessageFragment messageFragment, JSONObject json, Contact contact) {
        getDelegate().onExchangeMessages(this, json, contact.getInstance());
    }

    @Override
    public void notifyRemove(MessageFragment messageFragment, String domesticContact) {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " notifyRemove ()");
                    Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " ANTES DE REMOVER getMessageFragment.size() = " + getMessageFragments().size());
        getMessageFragments().remove(domesticContact);
                    Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " DEPOIS DE REMOVER getMessageFragment.size() = " + getMessageFragments().size());

    }

    /**
     * Notifica a contactFragment que uma instancia foi encontrada (found).
     *
     * @param instance
     */
    public void notifyFoundInstance(Instance instance) {
        getContactFragment().notifyFoundInstance(instance);
    }

    /**
     * Notiica a contactFragmente que uma instancia foi perdidada (lost).
     *
     * @param instance
     */
    public void notifyLostInstance(Instance instance) {
        getContactFragment().notifyLostInstance(instance);

//        removeMessageFragment(instance.getStringIdentifier());
    }

    /**
     * Inicia o fragment "MessageFragment" (janela de mensagens).
     *
     * @param contactFragment
     * @param contact
     * @param fragmentActivity
     */
    @Override
    public void callMessageFragment(ContactFragment contactFragment, final Contact contact, FragmentActivity fragmentActivity) {
        MessageFragment m1 = getMessageFragment(contact.getIdentifier());

        m1.setDestinataryContact(contact.getIdentifier());

        final android.support.v4.app.FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

//        fragmentManager.beginTransaction().add(R.id.id_container, m1, DemoDefs.MESSAGE_TAG).addToBackStack(DemoDefs.MESSAGE_TAG).commit();

        fragmentManager.beginTransaction().add(R.id.id_container, m1).addToBackStack(DemoDefs.MESSAGE_TAG).commit();

    }



}
