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

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hypelabs.demo.bundle.R;
import com.hypelabs.demo.bundle.database.ChatPersistence;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {
    private ArrayList<Contact> contacts;
    private final Activity act;
    private Context context;
    private WeakReference<AdapterDelegate> delegate;

    public ContactAdapter(ArrayList<Contact> contacts, Activity act, Context context, AdapterDelegate delegate) {
        this.contacts = contacts;
        this.act = act;
        this.context = context;
        this.delegate = new WeakReference<AdapterDelegate>(delegate);
    }

    private AdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    private Activity getActivity() {
        return act;
    }

    private ArrayList<Contact> getContacts() {
        return contacts;
    }

    private ChatPersistence getChatPersistence() {
        return Persistence.getInstance();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.line_of_contacts, parent, false);
        final TextView tvContactLine = (TextView) view.findViewById(R.id.tvContactLine);
        TextView tvCounterNewMessages = (TextView) view.findViewById(R.id.tvCounterNewMessages);

        tvCounterNewMessages.setVisibility(View.INVISIBLE); // textView do contador invisivel

        //Ação do click num contacto da listView
        //Notifica o delegate que invoca o "messageFragment" (janela de mensagens) do respetivo contacto
        tvContactLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDelegate().onSelectedContact(ContactAdapter.this, Persistence.getInstance().getContacts().get(position));
            }
        });

        tvContactLine.setText(getContacts().get(position).getIdentifier());

        return view;
    }
}
