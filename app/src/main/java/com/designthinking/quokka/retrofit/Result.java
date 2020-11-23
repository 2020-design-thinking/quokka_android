package com.designthinking.quokka.retrofit;

public enum Result {

    NO_SERVER, FAIL, SUCCESS, INUSE, RESERVED;

    public boolean isFail(){
        return this == FAIL || this == INUSE || this == RESERVED;
    }

}
