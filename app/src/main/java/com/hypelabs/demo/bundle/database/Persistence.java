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

package com.hypelabs.demo.bundle.database;

import android.content.Context;

import com.hypelabs.demo.bundle.chat.Contact;
import com.hypelabs.demo.bundle.chat.Message;
import com.hypelabs.hype.Instance;

import java.util.ArrayList;
import java.util.HashMap;

public class Persistence implements ChatPersistence, AlarmPersistence, InstancePersistence {

    private HashMap<String, Contact> mapContacts;
    private ArrayList<Contact> contacts;
    private HashMap<String, ArrayList<Message>> messages;
    private HashMap<String, Instance> instances;
    private static Persistence instance;

    public static Persistence getInstance() {
        if (instance == null) {
            instance = new Persistence();
        }
        return instance;
    }

    public void clearAll() {
        getContacts().clear();
        getMessages().clear();
    }

    public HashMap<String, ArrayList<Message>> getMessages() {
        if(messages == null){
            messages = new HashMap<String, ArrayList<Message>>();
        }
        return messages;
    }

    @Override
    public ArrayList<Contact> getContacts() {
        if(contacts == null){
            contacts = new ArrayList<Contact>();
        }
        return contacts;
    }

    @Override
    public ArrayList<Message> getMessages(String contactIdentifier) {
        if(getMessages().get(contactIdentifier) == null){
            getMessages().put(contactIdentifier, new ArrayList<Message>());
        }

        return getMessages().get(contactIdentifier);
    }

    @Override
    public HashMap<String, Instance> getInstances() {
        if (instances == null) {
            instances = new HashMap<String, Instance>();
        }
        return instances;
    }

    public HashMap<String, Contact> getMapContacts() {
        if(mapContacts == null){
            mapContacts = new HashMap<String, Contact>();
        }
        return mapContacts;
    }

    @Override
    public void addInstance(Instance inst) {
        getInstances().put(inst.getStringIdentifier(), inst);
    }

    @Override
    public void removeInstance(String instanceKey) {
        getInstances().remove(instanceKey);
    }

    @Override
    public void addContact(Contact contact)
    {
        if(getMapContacts().get(contact.getIdentifier()) != null){
            return;
        }

        getMapContacts().put(contact.getIdentifier(), contact);
        getContacts().add(contact);
    }

    @Override
    public void addMessage(String contactIdentifier, Message message)
    {
        if(getMessages().get(contactIdentifier) == null){
            ArrayList<Message> m = new ArrayList<Message>();
            m.add(message);
            getMessages().put(contactIdentifier, m);
        }
        else
        {
            ArrayList<Message> m = getMessages().get(contactIdentifier);
            m.add(message);
//            getMessages().put(contact.getIdentifier(), m);
        }
    }

    @Override
    public void removeContact(String contactKey)
    {
        if(getMapContacts().get(contactKey) == null){
            return;
        }
        Contact c = getMapContacts().remove(contactKey);
        getContacts().remove(c);
    }

//    @Override
//    public void removeMessage(String contactIdentifier, String messageKey)
//    {
//        for(int i = 0; i < getMessages().get(contactIdentifier).size(); i++){
//            if(getMessages().get(contactIdentifier).get(i).getIdentifier().equalsIgnoreCase(messageKey)){
//                getMessages().get(contactIdentifier).remove(i);
//                break;
//            }
//        }
//    }

    @Override
    public Contact getContact(String contactIdentifier) throws Exception {

        Contact c = getMapContacts().get(contactIdentifier);

        if(c != null){
            return c;
        }

        throw new Exception();
    }
}
