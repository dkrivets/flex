package org.simple.flex;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Work with configuraton files
 * @author DKrivets
 * @version 20181117
 */
public class Config {
    private Properties   props;
    private List<String> args; // List of public arguments

    /**
     * Simple config
     */
    public Config() {
        this.init();
    }

    /**
     * Config with cmd params
     * @param p_args CMD params
     */
    public Config( String... p_args ){
        this.init();
        this.changeValues( p_args );
    }

    /**
     * Init object. Uses in Config()
     */
    private void init(){
        this.props = new Properties();

        // Init list of public arguments
        this.args = Stream.of( "host", "port", "repository").collect(Collectors.toList());

        // Try to read data from config file
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
     * Sometimes we need to replace default configs values
     * @param p_args
     */
    private void changeValues( String... p_args ){
        Arrays.stream(p_args).forEach(arg -> {
            String[] parts = arg.split("=");
            String key   = parts[0].replace("--", "").toLowerCase();
            String value = parts[1];
            //
            if( this.args.contains( key ) ){
                this.props.setProperty( key, value );
            }
        } );
    }

    /**
     * Return property
     * @param p_strPropertyName
     * @return return property value
     */
    public String get( String p_strPropertyName ){
        return props.getProperty( p_strPropertyName );
    }

    /**
     * Return list of properties
     * @return this.props List of properties
     */
    public Properties getProperties() {
        return props;
    }
}