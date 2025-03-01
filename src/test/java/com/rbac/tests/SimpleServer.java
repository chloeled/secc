package com.rbac.tests;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

public class SimpleServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080); // Sur le port 8080
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Servir les fichiers statiques depuis "src/main/resources/static"
        context.setResourceBase("src/main/resources/static");
        context.addServlet(new ServletHolder("default", DefaultServlet.class), "/");

        server.setHandler(context);
        server.start();
        server.join();
    }
}
