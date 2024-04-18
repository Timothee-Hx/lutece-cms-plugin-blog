<jsp:useBean id="manageAdminDashboard" scope="session" class="fr.paris.lutece.plugins.blog.web.adminDashboard.BlogAdminDashboardJspBean" />
<%
  if("blog".equals(request.getParameter("plugin_name")))
    {
        response.sendRedirect( manageAdminDashboard.getDashboardPage(request) );
    }
    else
    {
        response.sendRedirect( manageAdminDashboard.getDashboardPage(request) );
    }
%>
