package org.simple.flex;

import org.apache.camel.main.Main;
import org.simple.flex.beans.ManageRouteBean;
import org.simple.flex.beans.TemplateBean;
import org.simple.flex.flows.Rest;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();

        // Management of flows
        main.addRouteBuilder( new Rest() );
        main.bind( "templateBean", new TemplateBean( main.getOrCreateCamelContext(), main.getRouteBuilders()) );
        main.bind( "manageBean", new ManageRouteBean( main.getRouteBuilders() ) );


        // Load flows
        FlowLoadFactory flf = new FlowLoadFactory( new FlowRepository() );
        flf.repo.add("~/Documents/PROJECTS/java/pure-test/target/flows/MyRouteBuilder.java");
        flf.load( main );


        main.run(args);

    }

}
