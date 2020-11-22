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
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hypelabs.demo.bundle.R;
import com.hypelabs.demo.bundle.database.ChatPersistence;
import com.hypelabs.demo.bundle.database.Persistence;
import com.hypelabs.demo.bundle.utils.Log;
import com.hypelabs.demo.bundle.utils.LogLevel;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private Activity act;
    private String domesticContact;
    private String destinataryContact;

    public MessageAdapter(int resource, Activity act, Context context, String domesticContact, String destinataryContact) {
        super(context, resource);
        this.act = act;
        this.context = context;
        this.domesticContact = domesticContact;
        this.destinataryContact = destinataryContact;
    }

    private ArrayList<Message> getMessages() {
        Log.Log(LogLevel.LevelInfo, getClass().getSimpleName() + " getMessages size: " + getChatPersistence().getMessages(getDestinataryContact()).size());
        return getChatPersistence().getMessages(getDestinataryContact());
    }

    public String getDomesticContact() {
        return domesticContact;
    }

    private String getDestinataryContact() {
        return destinataryContact;
    }

    private Activity getActivity() {
        return act;
    }

    @Override
    public int getCount() {
        return getMessages().size();
    }

    @Override
    public Message getItem(int position) {
        return getMessages().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Log.Log(LogLevel.LevelInfo, "Entrou no getView do adapter");

        if (getMessages().get(position).getOriginContactIdentifier().equalsIgnoreCase(getDomesticContact())) {
            Log.Log(LogLevel.LevelInfo, "Ta dentro de escreve á direita");
            // Se o emissor for eu, escrevo à direita

            view = getActivity().getLayoutInflater().inflate(R.layout.item_right, parent, false);
            final TextView tvMsgRight = (TextView) view.findViewById(R.id.tvMsgRight);
            final TextView tvDateRight = (TextView) view.findViewById(R.id.tvDateRight);
            final ImageView ivImageRight = (ImageView) view.findViewById(R.id.ivImageRight);

            if (getMessages().get(position).getMessageType() == MessageType.TEXT.getValue()) {
                tvMsgRight.setText(((TextMessage) getMessages().get(position)).getText());
                tvDateRight.setText(getMessages().get(position).getDate());
            } else if (getMessages().get(position).getMessageType() == MessageType.PHOTO.getValue()) {
                ivImageRight.setImageBitmap(((PhotoMessage) getMessages().get(position)).getImage());
                tvDateRight.setText(getMessages().get(position).getDate());

            }

        } else if (getMessages().get(position).getDestinyContactIdentifier().equalsIgnoreCase(getDomesticContact())) {
            Log.Log(LogLevel.LevelInfo, "Ta dentro de escreve á esquerda");
//            Log.Log(LogLevel.LevelInfo, "\n\nEstá dentro do escrever à esquerda\n");

            view = getActivity().getLayoutInflater().inflate(R.layout.item_left, parent, false);
            final TextView tvMsgLeft = (TextView) view.findViewById(R.id.tvMsgLeft);
            final TextView tvDateLeft = (TextView) view.findViewById(R.id.tvDateLeft);
            final ImageView ivImageLeft = (ImageView) view.findViewById(R.id.ivImageLeft);

            if (getMessages().get(position).getMessageType() == MessageType.TEXT.getValue()) {
                tvMsgLeft.setText(((TextMessage) getMessages().get(position)).getText());
                tvDateLeft.setText(getMessages().get(position).getDate());
            } else if (getMessages().get(position).getMessageType() == MessageType.PHOTO.getValue()) {
                ivImageLeft.setImageBitmap(((PhotoMessage) getMessages().get(position)).getImage());
                tvDateLeft.setText(getMessages().get(position).getDate());
            }
        }
        return view;
    }

    /**
     * Acede à persistencia.
     *
     * @return
     */
    private ChatPersistence getChatPersistence() {
        return Persistence.getInstance();
    }
}
