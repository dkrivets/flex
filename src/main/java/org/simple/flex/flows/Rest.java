package org.simple.flex.flows;

import org.apache.camel.model.rest.RestBindingMode;
import org.simple.flex.Flow;

/**
 * Rest flow for API
 * @version 20181103
 */
public class Rest extends Flow {
    public String Description = "REST flow ";
    public void configure() {
        // Rest configuration
        restConfiguration()
                .component("restlet")
                //.host("localhost").port("8080")
                .host("{{host}}").port("{{port}}")
                .bindingMode(RestBindingMode.auto);

        // API commands
        rest().path("/api")//.consumes("application/json").produces("application/json")
                .post("/{id}/stop")
                .to("bean:manageBean?method=stop(${header.id})")
                .post("/{id}/start")
                .to("bean:manageBean?method=start(${header.id})")
                .post("/{id}/delete")
                .to("bean:manageBean?method=delete(${header.id})");


        // Visualization
        // list of flows
        rest().path("/").consumes("text/html").produces("text/html")
                .get("/list")
                .to("bean:templateBean?method=run");
    }
}
