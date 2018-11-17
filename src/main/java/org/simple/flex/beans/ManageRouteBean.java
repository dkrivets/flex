package org.simple.flex.beans;

import org.apache.camel.Handler;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.builder.RouteBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.BiConsumer;

/**
 * Class to manage route: stop, start and delete by id.
 * @version 20181110
 */
public class ManageRouteBean {

    private List<RouteBuilder> ctx;

    /**
     * Class initialization
     * @param ctx{List<RouteBuilder>} List of RouteBuilder
     */
    public ManageRouteBean(List<RouteBuilder> ctx){
        this.ctx = ctx;
    }

    /**
     * Template to search route by id and do action with them.
     * Uses as decorator for others operation.
     * @param id {String} Route id
     * @param lambda {BiConsumer} lambda with 2 params
     * @return result of lambda-action as json-string
     */
    private String template( String id, BiConsumer<RouteBuilder,RouteDefinition> lambda ){
        String result = null;

        try {
            // Filtered list of routes
            List<RouteBuilder> allRoutes = this.ctx.stream()
                    .filter(i -> i.getClass().getName().equals(id))
                    .collect( Collectors.toList() );

            // Apply lambda to rest of list of routes
            if( allRoutes.size() > 0 ){
                allRoutes.forEach(i -> {
                            i.getRouteCollection().getRoutes().forEach(v -> {
                                lambda.accept( i, v );
                                System.out.println(id + ":" + v.getId() + " operation has been done." );
                            });
                        });
                result = "{'result': 'done'}";
            }
            else{
                result = "{'result': 'warning', 'message': 'No one route hasn't been found for operation.'}";
            }
        }
        catch(Exception ex){
            result = "{'result': 'error', 'message': '" + ex.getMessage() + "' }";
        }

        return result;
    }

    /**
     * Delete route by id.
     * @param id {String} Route id
     * @return result of operation as json-string
     */
    @Handler
    public String delete( String id ){
        BiConsumer<RouteBuilder,RouteDefinition> biConsumer = (i, v) -> {
            try {
                i.getContext().stopRoute(v.getId());
                i.getContext().getShutdownRoute();
                i.getContext().removeRoute(v.getId());
                i.getContext().removeRouteDefinition(v);
            }
            catch(Exception ex){ throw new RuntimeException(ex); }
        };

        return this.template( id, biConsumer );
    }

    /**
     * Start route by id
     * @param id {String} Route id
     * @return result of operation as json-string
     */
    @Handler
    public String start( String id ){
        BiConsumer<RouteBuilder,RouteDefinition> biConsumer = (i, v) -> {
            try {
                i.getContext().startRoute( v.getId() );
            }
            catch(Exception ex){
                throw new RuntimeException(ex);
            }
        };

        return this.template( id, biConsumer );
    }

    /**
     * Stop route by id.
     * @param id {String} Route id
     * @return result of operation as json-string
     */
    @Handler
    public String stop( String id ){
        BiConsumer<RouteBuilder,RouteDefinition> biConsumer = (i, v) -> {
            try {
                i.getContext().stopRoute( v.getId() );
            }
            catch(Exception ex){
                throw new RuntimeException(ex);
            }
        };

        return this.template( id, biConsumer );
    }
}