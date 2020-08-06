package org.tensorflow.lite.examples.detection.db_firebase;

import androidx.annotation.Keep;

@Keep
public class EmergencyContact {
    String name;
    String number;

    public EmergencyContact() {
    }

    public EmergencyContact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}