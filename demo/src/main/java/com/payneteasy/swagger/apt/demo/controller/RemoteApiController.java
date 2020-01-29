package com.payneteasy.swagger.apt.demo.controller;

import com.payneteasy.swagger.apt.gen.ArgumentsParseStrategy;
import com.payneteasy.swagger.apt.gen.ServiceInfo;
import com.payneteasy.swagger.apt.gen.ServiceInvoker;
import com.payneteasy.swagger.apt.gen.ServiceMethodId;
import com.payneteasy.swagger.apt.gen.ServiceMethodInfo;
import com.payneteasy.swagger.apt.gen.ServiceUriUtil;
import com.payneteasy.swagger.apt.gen.SwaggerGenerator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Controller
public class RemoteApiController {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteApiController.class);

    /**
     * Map:
     * <ul>
     * <li>key - service name</li>
     * <li>value - service info</li>
     * </ul>
     */
    private final Map<String, ServiceInfo> services;
    private final SwaggerGenerator         generator;
    private final ServiceInvoker           serviceInvoker;

    @Autowired
    public RemoteApiController(BeanFactory aContext) throws ClassNotFoundException {
        this(aContext, "com.payneteasy.swagger.apt.demo.service");
    }

    RemoteApiController(BeanFactory context, String basePackage) throws ClassNotFoundException {
        services       = ServiceInfoSearcher.searchServices(context, basePackage);
        serviceInvoker = new ServiceInvoker(services, ArgumentsParseStrategy.MIXED);
        generator      = new SwaggerGenerator("demo services", "1.0", "/demo/api", ArgumentsParseStrategy.MIXED);
        LOG.info("Exported {} service. See /demo/api/doc/", services.size());
    }

    @RequestMapping({"/swagger-ui/demo.json"})
    public ModelAndView paynetUiJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        URL url = new URL(request.getRequestURL().toString());
        //request to http://localhost/demo/.. (nginx -> demo):
        //server port: 80
        //local port:  8080
        response.getWriter().println(
                generator.generateJson(services.values(), url.getProtocol(), url.getHost(), request.getServerPort())
        );
        return null;
    }

    @RequestMapping({"/api/**"})
    public ModelAndView processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final ServiceMethodId serviceMethodId = ServiceUriUtil.fromUri(request.getRequestURI());
            final String encoding =
                    (request.getCharacterEncoding() != null) ? request.getCharacterEncoding() : StandardCharsets.UTF_8.name();
            final String argumentsJson = IOUtils.toString(request.getInputStream(), encoding);
            final String result        = serviceInvoker.invokeServiceMethod(serviceMethodId, argumentsJson);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().println(result);
        } catch (Exception e) {
            final UUID uuid = UUID.randomUUID();
            LOG.error(String.format("Error while processing %s. Unexpected error uuid=%s.", request.getRequestURI(), uuid), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(String.format("{\"errorId\":\"%s\"}", uuid));
        }

        return null;
    }

    @RequestMapping({"/api/doc"})
    public ModelAndView listServicesRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("doc/");
        return null;
    }

    @RequestMapping({"/api/doc/"})
    public ModelAndView listServices(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<ol>");
        for (Map.Entry<String, ServiceInfo> entry : services.entrySet()) {
            out.printf("<li><a href='%s'>%s</a></li>\n", entry.getKey(), entry.getKey());
        }

        return null;
    }

    @RequestMapping({"/api/doc/**"})
    public ModelAndView listMethods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        String[]    paths       = request.getRequestURI().split("/");
        ServiceInfo serviceInfo = services.get(paths[paths.length - 1]);
        PrintWriter out         = response.getWriter();
        out.println("<ol>");
        for (ServiceMethodInfo serviceMethodInfo : serviceInfo.methods.values()) {
            out.printf("<li>%s</li>\n", serviceMethodInfo.method);
        }
        return null;
    }

}
