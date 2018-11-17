package org.simple.flex;

import java.util.List;
import java.util.ArrayList;

public class FlowRepository {
    private List<String> paths;

    public FlowRepository(){
         paths = new ArrayList<String>();
    }

    public void add( String path ){
        paths.add( path );
    }

    public List<String> list(){
        return paths;
    }
}
