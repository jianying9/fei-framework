package com.fei.web.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fei.app.utils.ToolUtil;
import com.fei.web.response.Response;
import com.fei.web.router.Router;
import com.fei.web.router.RouterContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
@WebServlet(name = "fei-app-http", loadOnStartup = 1, urlPatterns = {"/api/*"}, asyncSupported = false)
public class AppServlet extends HttpServlet
{

    private final Logger logger = LoggerFactory.getLogger(AppServlet.class);

    @Override
    public void init() throws ServletException
    {
        this.logger.info("AppServlet start.....");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        this.setResponseHeader(response);
        //读取输入数据
        String contentType = request.getContentType();
        JSONObject input = null;
        if (contentType != null && contentType.contains("application/json")) {
            // 读取json
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            try {
                Object obj = JSON.parse(sb.toString());
                if (obj instanceof JSONObject) {
                    input = (JSONObject) obj;
                }
            } catch (JSONException e) {
                this.logger.warn("json error:{}:{}", e.getMessage(), line);
            }
        } else {
            //读取传统数据
            input = new JSONObject();
            Enumeration<String> names = request.getParameterNames();
            String name;
            String value;
            while (names.hasMoreElements()) {
                name = names.nextElement();
                value = request.getParameter(name);
                value = ToolUtil.trim(value);
                input.put(name, value);
            }
        }
        //执行
        if (input == null) {
            input = new JSONObject();
        }
        //
        String route = request.getPathInfo();
        if (route == null || route.isEmpty() || route.equals("/")) {
            String _api = input.getString("_api");
            if ("true".equals(_api)) {
                JSONObject output = RouterContext.INSTANCE.getApi();
                this.toWrite(response, output.toJSONString());
            } else {
                //route不存在
                JSONObject output = Response.createOk();
                this.toWrite(response, output.toJSONString());
            }
        } else {
            Router router = RouterContext.INSTANCE.get(route);
            if (router == null) {
                //route不存在
                JSONObject output = Response.createNotfound(route);
                this.toWrite(response, output.toJSONString());
            } else {
                //获取auth信息
                String auth = request.getHeader("Authorization");
                String content = router.processRequest(input, auth);
                //响应
                this.toWrite(response, content);
            }
        }
    }

    private void toWrite(HttpServletResponse response, String jsonStr)
    {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(jsonStr);
            printWriter.flush();
        } catch (IOException e) {
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.setResponseHeader(response);
    }

    private void setResponseHeader(HttpServletResponse response)
    {
        //跨域设置
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.addHeader("Access-Control-Allow-Headers", "authorization,content-type");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "fei-app-servlet";
    }

}
