package fr.paris.lutece.plugins.blog.web.adminDashboard;

import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.plugins.blog.business.BlogAdminDashboardHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.plugins.blog.web.utils.BlogConstant;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
/**
 * This class provides the user interface to manage general Blog feature in the site admin dashboard ( modify mandatory tag number )
 */
@Controller( controllerJsp = "ManageAdminDashboard.jsp", controllerPath = "jsp/admin/plugins/blog/", right = "BLOG_AVANCED_CONFIGURATION" )

public class BlogAdminDashboardJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 3045411044102177294L;
    private static final String DASHBOARD_PAGE = "jsp/admin/AdminTechnicalMenu.jsp";
    private static final String ACCESS_DENIED_MESSAGE = "portal.message.user.accessDenied";
    public static final String RIGHT_AVANCED_CONFIGURATION = "BLOG_AVANCED_CONFIGURATION";
    private static final String ACTION_UPDATE_MANDATORY_TAG_NUMBER = "updateMandatoryTagNumber";

    private static final String PARAMETER_NUMBER_MANDATORY_TAGS = "numberMandatoryTag";
    private static final String DASHBOARD_PAGE_TAG = "?tab=modifyMandatoryBlogTagNumber";
    private static final String PARAMETER_MAX_PUBLICATION_DATE_VALUE = "maxPublicationDate";
    private static final String DASHBOARD_PAGE_MAX_PUBLICATION_DATE = "?tab=manageMaxPublicationDate";
    private static final String ACTION_MANAGE_MAX_PUBLICATION_DATE = "manageMaxPublicationDate";
    private static final String VIEW_MANAGE_AVANCED_CONFIGURATIONS = "manageAvancedConfigurations";

    @View(value = VIEW_MANAGE_AVANCED_CONFIGURATIONS, defaultView = true)
    public String getManageAdminDashboardView( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser adminUser = AdminUserService.getAdminUser( request );
        if ( !adminUser.checkRight( RIGHT_AVANCED_CONFIGURATION ) )
        {
            String strMessage = I18nService.getLocalizedString( ACCESS_DENIED_MESSAGE, getLocale( ) );
            throw new AccessDeniedException( strMessage );
        } else
        {
            String baseUrl = AppPathService.getBaseUrl( request );
            UrlItem url = new UrlItem( baseUrl + DASHBOARD_PAGE );

            return redirect( request, url.getUrl( ) );
        }
    }
    /**
     * {@inheritDoc}
     */

    @Action( ACTION_UPDATE_MANDATORY_TAG_NUMBER )
    public String updateMandatoryTagNumber( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser adminUser = AdminUserService.getAdminUser( request );
        if ( !adminUser.checkRight( RIGHT_AVANCED_CONFIGURATION ) )
        {
            String strMessage = I18nService.getLocalizedString( ACCESS_DENIED_MESSAGE, getLocale( ) );
            throw new AccessDeniedException( strMessage );
        }
        int nNumberMandatoryTags = 0;
        if( request.getParameter( PARAMETER_NUMBER_MANDATORY_TAGS ) != null)
        {
            nNumberMandatoryTags =  Integer.parseInt( request.getParameter( PARAMETER_NUMBER_MANDATORY_TAGS ) );
        }
 String idDashboardStr = AppPropertiesService.getProperty(BlogConstant.PROPERTY_ADVANCED_MAIN_DASHBOARD_ID);
        int idDashboard = (idDashboardStr != null) ? Integer.parseInt(idDashboardStr) : 1;
        BlogAdminDashboardHome.updateNumberMandatoryTags(idDashboard, nNumberMandatoryTags );
        String strUrl = AppPathService.getBaseUrl( request ) + DASHBOARD_PAGE + DASHBOARD_PAGE_TAG;
        return redirect( request, strUrl);
    }
    /**
     * Manage the maximum publication date for a blog post
     * @param request
     * @return
     * @throws AccessDeniedException
     * @throws java.text.ParseException
     */
    @Action( ACTION_MANAGE_MAX_PUBLICATION_DATE )
    public String manageMaxPublicationDate( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser adminUser = AdminUserService.getAdminUser( request );
        if ( !adminUser.checkRight( RIGHT_AVANCED_CONFIGURATION ) )
        {
            String strMessage = I18nService.getLocalizedString( ACCESS_DENIED_MESSAGE, getLocale( ) );
            throw new AccessDeniedException( strMessage );
        }
        if( request.getParameter( PARAMETER_MAX_PUBLICATION_DATE_VALUE ) != null)
        {
            String strMaxPublicationDate = request.getParameter( PARAMETER_MAX_PUBLICATION_DATE_VALUE );
     String idDashboardStr = AppPropertiesService.getProperty(BlogConstant.PROPERTY_ADVANCED_MAIN_DASHBOARD_ID);
        int idDashboard = (idDashboardStr != null) ? Integer.parseInt(idDashboardStr) : 1;
        BlogAdminDashboardHome.updateMaximumPublicationDate(idDashboard, formatStringToSqlDate(strMaxPublicationDate) );
        }
        String strUrl = AppPathService.getBaseUrl( request ) + DASHBOARD_PAGE + DASHBOARD_PAGE_MAX_PUBLICATION_DATE;
        return redirect( request, strUrl );
    }

    public java.sql.Date formatStringToSqlDate(String strDate)
    {
        java.sql.Date sqlDate = null;
        try
        {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date utilDate = sdf.parse(strDate);
            sqlDate = new java.sql.Date(utilDate.getTime());
        }
        catch (ParseException e)
        {
            AppLogService.error("Error parsing date", e);
        }
        return sqlDate;
    }
}
