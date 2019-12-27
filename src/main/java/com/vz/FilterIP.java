package com.vz;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.*;
import java.util.TreeSet;

@WebFilter(filterName = "filter", urlPatterns = {"/*"})
public class FilterIP implements Filter {

    static File file;
    static TreeSet<String> setIP = new TreeSet<>();
    static String blackListIP = "";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        file = new File(filterConfig.getServletContext().getRealPath("/WEB-INF/blacklist.txt"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        String currentIP = servletRequest.getRemoteAddr();
        boolean isInBlacklist = false;

        if(servletRequest.getParameter("add")!=null){
            setIP.add(servletRequest.getParameter("add").trim());
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader readerFile = new BufferedReader(fileReader)) {

                while ((blackListIP = readerFile.readLine()) != null) {
                    setIP.add(blackListIP.trim());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                servletResponse.getWriter().println("Can not check out IP! File not found!");
            }
            try (FileWriter fileWriter = new FileWriter(file);
                 PrintWriter writerFile = new PrintWriter(fileWriter)) {
                blackListIP="";
                for (String str:setIP) {
                    blackListIP+=str+"\n";
                }
                writerFile.write(blackListIP);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                servletResponse.getWriter().println("Can not check out IP! File not found!");
            }

        }else if(servletRequest.getParameter("del")!=null){

            try (FileReader fileReader = new FileReader(file);
                 BufferedReader readerFile = new BufferedReader(fileReader)) {

                while ((blackListIP = readerFile.readLine()) != null) {
                    setIP.add(blackListIP.trim());
                }
                setIP.remove(servletRequest.getParameter("del").trim());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                servletResponse.getWriter().println("Can not check out IP! File not found!");
            }
            try (FileWriter fileWriter = new FileWriter(file);
                 PrintWriter writerFile = new PrintWriter(fileWriter)) {
                blackListIP="";
                for (String str:setIP) {
                    blackListIP+=str+"\n";
                }
                writerFile.write(blackListIP);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                servletResponse.getWriter().println("Can not check out IP! File not found!");
            }

        }

        try (FileReader fileReader = new FileReader(file);
             BufferedReader readerFile = new BufferedReader(fileReader)) {

            while ((blackListIP = readerFile.readLine()) != null) {
                if (currentIP.equals(blackListIP.trim())) {
                    isInBlacklist = true;
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            servletResponse.getWriter().println("Can not check out IP! File not found!");
        }

        if (isInBlacklist) {
            servletResponse.getWriter().println("Access disallowed\n" + currentIP);
        } else {
            servletResponse.getWriter().println(currentIP);
            filterChain.doFilter(servletRequest, servletResponse);
        }



    }

    @Override
    public void destroy() {

    }
}
