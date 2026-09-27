package com.msa.customer.core;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class CustomerCoreApplication {

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
