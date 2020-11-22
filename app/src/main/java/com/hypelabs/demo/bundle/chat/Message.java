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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public abstract class Message {

    private String originContactIdentifier;
    private String destinyContactIdentifier;
    private String date;
    private String identifier;
    private boolean isRead;

    public Message(String originContactIdentifier, String destinyContactIdentifier){
        this.identifier = UUID.randomUUID().toString();
        this.originContactIdentifier = originContactIdentifier;
        this.destinyContactIdentifier = destinyContactIdentifier;
        this.date = initDate();
        this.isRead = false;
    }

    public String initDate(){
        Date date = new Date();
        DateFormat formato = new SimpleDateFormat("dd/MM/yy - HH:mm");

        return formato.format(date);
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public Boolean isRead()
    {
        return this.isRead;
    }

    public void notifyMessageRead()
    {
        this.isRead = true;
    }

    public abstract int getMessageType();

    public String getOriginContactIdentifier() {
        return originContactIdentifier;
    }

    public String getDestinyContactIdentifier() {
        return destinyContactIdentifier;
    }

    public String getDate() {
        return date;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                ", originContactIdentifier='" + originContactIdentifier + '\'' +
                ", destinyContactIdentifier='" + destinyContactIdentifier + '\'' +
                ", date=" + date +
                ", identifier='" + identifier + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
