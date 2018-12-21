package com.nastia.cf;

import java.util.ArrayList;
import java.util.Objects;

public class Contact {
    String myName = "";
    String myNumber = "";

    public Contact(String name, String phone) {
        this.myName = name;
        this.myNumber = phone;
    }

    public String getName() {
        return myName;
    }


    public String getPhoneNum() {
        return myNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(myName, contact.myName) &&
                Objects.equals(myNumber, contact.myNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(myName, myNumber);
    }
}
