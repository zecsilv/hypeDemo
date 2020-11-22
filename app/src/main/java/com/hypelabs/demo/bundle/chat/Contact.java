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

import com.hypelabs.hype.Instance;

import java.util.UUID;

public class Contact {
    private String name;
    private String photo;
    private Instance instance;
    private String identifier;

    public Contact(Instance instance, String photo) {
        this.instance = instance;
        this.name = UUID.randomUUID().toString(); // Random de caracteres alfanumericos.
        this.identifier = instance.getStringIdentifier();
        this.photo = photo;
    }

    public Contact(Instance instance) {
        this.instance = instance;
        this.name = UUID.randomUUID().toString(); // Random de caracteres alfanumericos.
        this.identifier = instance.getStringIdentifier();
    }

    public String getIdentifier() {
        return identifier;
    }

    public Instance getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String toString() {
        return "name: " + name + "contact identifier: " + identifier;
    }

}
