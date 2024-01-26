package com.uaepay.gateway.cgs.web.servlet;

import com.uaepay.gateway.cgs.ext.service.ReceiveOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@WebServlet(urlPatterns = "/api/*")
public class ReceiveOrderServlet extends HttpServlet {

    @Autowired
    ReceiveOrderService receiveOrderService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        receiveOrderService.processHttpRequest(req, resp);
    }

}
