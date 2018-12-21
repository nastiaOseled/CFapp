package com.nastia.cf;

import java.util.ArrayList;

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


}
