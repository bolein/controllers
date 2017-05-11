package com.cvvm;

import okhttp3.*;

/**
 * Created by Vadim Ovcharenko
 * 01.11.2016.
 */

interface ExecutionContext {
    void attachPromise(PromiseInternal promise);
    void detachPromise(PromiseInternal promise);
}
