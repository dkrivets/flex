package org.simple.flex;

import org.apache.camel.main.Main;

public class FlowLoadFactory {
    public FlowRepository repo;
    //private Main addBuilder;

    public  FlowLoadFactory( FlowRepository repo ){
        this.repo = repo;
        //this.addBuilder = addBuilder;
    }

    public void load( Main addBuilder ){
        String ext;
        for( String filepath : repo.list() ){
            try {
                addBuilder.addRouteBuilder( new FlowLoader(filepath).run() );
            }
            catch(Exception ex){
                ext = ex.getMessage();
            }
        }
    }
}
