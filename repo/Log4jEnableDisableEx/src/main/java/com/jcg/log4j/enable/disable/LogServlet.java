package com.jcg.log4j.enable.disable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("DanglingJavadoc")
@WebServlet("/loggingServlet")
public class LogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    static Logger logger = Logger.getLogger(LogServlet.class);

    /***** @see HttpServlet#HttpServlet() *****/
    public LogServlet() {
        super();
    }

    /***** @see doPost(HttpServletRequest req, HttpServletResponse resp) *****/
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    /***** @see handleRequest(HttpServletRequest req, HttpServletResponse resp) *****/
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("!.... Application Process Is Started ....!");

        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        String[] value = req.getParameterValues("log4jMode");

        if (value != null) {
            for (String mode : value) {
                logger.debug("Reading Log4j Enable Or Disable Decision?= " + mode);

                /**** Setting Log4j Priority Mode As 'DEBUG' ****/
                if (mode.equalsIgnoreCase("DEBUG")) {
                    logger.setLevel(Level.DEBUG);
                    logger.debug("Enabled 'DEBUG' Mode ....!");
                }

                /**** Setting Log4j Priority Mode As 'INFO' ****/
                else if (mode.equalsIgnoreCase("INFO")) {
                    logger.setLevel(Level.INFO);
                    logger.info("Enabled 'INFO' Mode ....!");
                }

                /**** Setting Log4j Priority Mode As 'WARN' ****/
                else if (mode.equalsIgnoreCase("WARN")) {
                    logger.setLevel(Level.WARN);
                    logger.warn("Enabled 'WARN' Mode ....!");
                }

                /**** Setting Log4j Priority Mode As 'ERROR' ****/
                else if (mode.equalsIgnoreCase("ERROR")) {
                    logger.setLevel(Level.ERROR);
                    logger.error("Enabled 'ERROR' Mode ....!");
                }

                /**** Setting Log4j Priority Mode As 'OFF' ****/
                else {
                    logger.setLevel(Level.OFF);
                }
                logger.debug("!.... Application Process Is Completed ....!");
                writer.println("<h1>Selected Log4j Mode Is " + mode + ".</h1>");
            }
        }
        writer.close();
    }

    public static void main(String[] args) {
    }
}