package org.simple.flex;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * Work with configuraton files
 * @author DKrivets
 * @version 20181117
 */
public class Config {
    private Properties props;

    public Config() {
        props = new Properties();

        InputStream fis = null;
        try{
            fis = getClass().getClassLoader().getResourceAsStream( "config.properties" );
            props.load( fis );
        }
        catch( Exception ex ){
            ex.printStackTrace();
        }
        finally{
            try {
                if (fis != null) fis.close();
            }
            catch( IOException ex ){
                ex.printStackTrace();
            }
        }
    }

    /**
     * Return property
     * @param strPropertyName
     * @return return property value
     */
    public String get( String strPropertyName ){
        return props.getProperty( strPropertyName );
    }
}
