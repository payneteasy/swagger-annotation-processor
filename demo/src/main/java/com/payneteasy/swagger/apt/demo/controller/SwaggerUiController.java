package com.payneteasy.swagger.apt.demo.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Controller
public class SwaggerUiController {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerUiController.class);

    @RequestMapping({"/swagger-ui/**", "/swagger-ui"})
    public ModelAndView swaggerUi(HttpServletRequest aRequest, HttpServletResponse aResponse) throws IOException {
        String filename = aRequest.getRequestURI().substring("/demo/swagger-ui".length());
        if (filename.length() == 0) {
            aResponse.sendRedirect("swagger-ui/");
            return null;
        }
        if (filename.length() == 1) {
            filename = "/index.html";
        }
        String      resource = "/swagger-ui" + filename;
        InputStream in       = SwaggerUiController.class.getResourceAsStream(resource);
        if (in == null) {
            LOG.warn("Resource {} -> {} not found", aRequest.getRequestURI(), resource);
            aResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + resource + " not found\n");
            return null;
        }

        if (filename.endsWith(".html")) {
            aResponse.setContentType("text/html; charset=UTF-8");
        } else if (filename.endsWith(".css")) {
            aResponse.setContentType("text/css; charset=UTF-8");
        } else if (filename.endsWith(".js")) {
            aResponse.setContentType("application/javascript; charset=UTF-8");
        } else {
            aResponse.setContentType("plain/text");
        }

        String html = IOUtils.toString(in, StandardCharsets.UTF_8.name());

        IOUtils.write(html, aResponse.getOutputStream());
        return null;
    }

}
