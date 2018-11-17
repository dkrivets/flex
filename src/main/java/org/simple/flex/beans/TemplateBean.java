package org.simple.flex.beans;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Handler;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

/**
 * Working with html-template to visualize and manage flows
 *
 * @version 20181103
 */
public class TemplateBean {
  private final String EXCLUDE_CLASS_REST = "ru.dkrivets.camel.Res2t1";

  private List<RouteBuilder> rbs; // List of RouteBuilder
  private CamelContext cxt; // C-X-T
  // (setq indent-tabs-mode nil)
  // (setq tab-width 4)
  /**
   * Class initialization
   *
   * @param ctx {CamelContext}
   * @param rbs {List<RouteBuilder>}
   */
  public TemplateBean(CamelContext ctx, List<RouteBuilder> rbs) {
    this.cxt = ctx;
    this.rbs = rbs;
  }

  /**
   * Get status of RouteDefinition
   *
   * @param rb {RouteBuilder} Route
   * @return
   */
  private ServiceStatus getStatus(RouteBuilder rb) {
    RouteDefinition rd = rb.getRouteCollection().getRoutes().stream().findAny().orElse(null);

    return rd != null ? rd.getStatus(rb.getContext()) : null;
  }

  @Handler
  public String run() {
    String result = "";
    InputStream stream = this.getClass().getClassLoader().getResourceAsStream("mustache/list.html");
    if (stream != null) {
      try {
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile(reader, "template");

        List<Object> list = new ArrayList<>();

        for (RouteBuilder item : this.rbs) {
          Class<? extends RouteBuilder> c = item.getClass();
          // Class c = item.getClass();
          if (!c.getName().equals(this.EXCLUDE_CLASS_REST)) {
            Map<String, Object> process = new HashMap<>();
            process.put("name", c.getName());
            String desc = "";
            try {
              java.lang.reflect.Field field = c.getField("Description");
              field.setAccessible(true);
              desc = (field.get(item)).toString();
            } catch (NoSuchFieldException e) {
              desc = "";
            } catch (IllegalAccessException e) {
              desc = "";
            }
            process.put("desc", desc);
            process.put("status", this.getStatus(item).name());
            process.put("isStarted", this.getStatus(item).isStarted());
            process.put("isStopped", this.getStatus(item).isStopped());
            process.put("isSuspended", this.getStatus(item).isSuspended());

            list.add(process);
          }
        }
        Map<String, Object> processes = Collections.singletonMap("process", list);

        final StringWriter writer = new StringWriter();
        m.execute(writer, processes);
        // Clear the streams
        reader.close();
        stream.close();

        result = writer.toString();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return result;
  }
}
