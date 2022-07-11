package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;

public interface ICheckSSL {
    void hasSSL(boolean hasSSL);
    void checkSSLError(ErrorData errorData);
}
