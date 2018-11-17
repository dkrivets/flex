package org.simple.flex;

import org.apache.camel.builder.RouteBuilder;

public abstract class Flow extends RouteBuilder{
    public String Description;
    // Constructor
    public Flow(){
        super();
        this.Description = "";
    }

    //Override
    public abstract void configure() ;
}