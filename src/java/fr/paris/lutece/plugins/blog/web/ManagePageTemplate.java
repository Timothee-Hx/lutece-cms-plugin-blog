package fr.paris.lutece.plugins.blog.web;

import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.filesystem.UploadUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.plugins.blog.business.BlogPageTemplate;
import fr.paris.lutece.plugins.blog.service.BlogFileService;
import  fr.paris.lutece.plugins.blog.business.BlogPageTemplateHome;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.portal.web.admin.AdminPageJspBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class provides the user interface to manage Blog features ( manage, create, modify, remove )
 */
@fr.paris.lutece.portal.util.mvc.admin.annotations.Controller( controllerJsp = "ManageBlogPageTemplate.jsp", controllerPath = "jsp/admin/plugins/blog/", right = "BLOG_MANAGEMENT" )
public class ManagePageTemplate extends fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean
{
    private static final long serialVersionUID = 1L;
    private static final String ACTION_ADD_PAGE_TEMPLATE = "addPageTemplate";
    private static final String PARAMETER_PAGE_TEMPLATE_TEMPLATE_FILE = "page_template_file";
    private static final String PARAMETER_PAGE_TEMPLATE_TEMPLATE_PICTURE_FILE = "page_template_picture_file";
    private static final String PARAMETER_PAGE_TEMPLATE_CONTENT = "page_template_content";
    private static final String PARAMETER_PAGE_TEMPLATE_CODE = "page_template_code";
    private static final String PARAMETER_PAGE_TEMPLATE_DESCRIPTION = "page_template_description";
    private static final String PARAMETER_PAGE_TEMPLATE_PORTLET_TYPE = "page_template_portlet_type";
    private static final String PARAMETER_PAGE_ID = "page_id";



    /**
     * Add page template
     *
     * @param request
     * @return Json The Json succes or echec
     * @throws ParseException
     */
    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( ACTION_ADD_PAGE_TEMPLATE )
    public String addPageTemplate( javax.servlet.http.HttpServletRequest request ) throws java.text.ParseException
    {
        BlogPageTemplate blogPageTemplate = new BlogPageTemplate( );
        String page_id = request.getParameter( PARAMETER_PAGE_ID );
        // recovers request
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
            FileItem templateItem = multi.getFile( PARAMETER_PAGE_TEMPLATE_TEMPLATE_FILE );
            String strTemplateFileName = templateItem == null ? null : UploadUtil.cleanFileName( templateItem.getName( ) );
            FileItem pictureItem = multi.getFile( PARAMETER_PAGE_TEMPLATE_TEMPLATE_PICTURE_FILE );
            String strPictureFileName = pictureItem == null ? null : UploadUtil.cleanFileName( pictureItem.getName( ) );
            String strDescription = multi.getParameter( PARAMETER_PAGE_TEMPLATE_DESCRIPTION );
            String strPortletType = multi.getParameter( PARAMETER_PAGE_TEMPLATE_PORTLET_TYPE );
            blogPageTemplate.setDescription( strDescription );
            blogPageTemplate.setPortletType( strPortletType );
            String strTemplateFileKey = null;
            // Retrieve data from session
            if ( StringUtils.isEmpty( strPortletType ) || StringUtils.isEmpty(
                    strDescription ) || strTemplateFileName == null || templateItem == null || StringUtils.isEmpty(
                    strTemplateFileName ) || !FileUtil.hasHtmlExtension( strTemplateFileName ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }
            else
            {
                try
                {
                    strTemplateFileKey = BlogFileService.storeFileItem( templateItem );
                }
                catch( Exception e )
                {
                    AppLogService.error( "Error during the storage of the file " + strTemplateFileName, e );
                   return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
                }
            }
            String strPictureFileKey = null;

            if ( pictureItem == null || StringUtils.isEmpty( strPictureFileName ) || !FileUtil.hasImageExtension( strPictureFileName ) )
            {
                AppLogService.info( "No picture file" );
            }
            else
            {
                try
                {
                    strPictureFileKey = BlogFileService.storeFileItem( pictureItem );
                }
                catch( Exception e )
                {
                    AppLogService.error( "Error during the storage of the file " + strPictureFileName, e );
                   return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
                }
            }
            blogPageTemplate.setFileKey( strTemplateFileKey );
            blogPageTemplate.setPictureKey( strPictureFileKey );
            BlogPageTemplateHome.create( blogPageTemplate );
        }

        // url to redirect to
        String url = getPageUrl( Integer.parseInt( page_id ) );
        return url;
    }
    public static HttpServletRequest staticAddPageTemplate (HttpServletRequest request, HttpServletRequest retrievedRequest) throws java.text.ParseException{
        BlogPageTemplate blogPageTemplate = new BlogPageTemplate( );
        String page_id = request.getParameter( PARAMETER_PAGE_ID );
        // recovers request
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
            FileItem templateItem = multi.getFile( PARAMETER_PAGE_TEMPLATE_TEMPLATE_FILE );
            String strTemplateFileName = templateItem == null ? null : UploadUtil.cleanFileName( templateItem.getName( ) );
            FileItem pictureItem = multi.getFile( PARAMETER_PAGE_TEMPLATE_TEMPLATE_PICTURE_FILE );
            String strPictureFileName = pictureItem == null ? null : UploadUtil.cleanFileName( pictureItem.getName( ) );
            String strDescription = multi.getParameter( PARAMETER_PAGE_TEMPLATE_DESCRIPTION );
            String strPortletType = multi.getParameter( PARAMETER_PAGE_TEMPLATE_PORTLET_TYPE );
            blogPageTemplate.setDescription( strDescription );
            blogPageTemplate.setPortletType( strPortletType );
            String strTemplateFileKey = null;
            // Retrieve data from session
            if ( StringUtils.isEmpty( strPortletType ) || StringUtils.isEmpty(
                    strDescription ) || strTemplateFileName == null || templateItem == null || StringUtils.isEmpty(
                    strTemplateFileName ) || !FileUtil.hasHtmlExtension( strTemplateFileName ) )
            {
                retrievedRequest.setAttribute("errorMessage", AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ));
                return retrievedRequest;
            }
            else
            {
                try
                {
                    strTemplateFileKey = BlogFileService.storeFileItem( templateItem );
                }
                catch( Exception e )
                {
                    AppLogService.error( "Error during the storage of the file " + strTemplateFileName, e );
                    retrievedRequest.setAttribute("errorMessage", AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ));
                    return retrievedRequest;
                }
            }
            String strPictureFileKey = null;

            if ( pictureItem == null || StringUtils.isEmpty( strPictureFileName ) || !FileUtil.hasImageExtension( strPictureFileName ) )
            {
                AppLogService.info( "No picture file" );
            }
            else
            {
                try
                {
                    strPictureFileKey = BlogFileService.storeFileItem( pictureItem );
                }
                catch( Exception e )
                {
                    AppLogService.error( "Error during the storage of the file " + strPictureFileName, e );
                    retrievedRequest.setAttribute("errorMessage", AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ));
                    return retrievedRequest;
                }
            }
            blogPageTemplate.setFileKey( strTemplateFileKey );
            blogPageTemplate.setPictureKey( strPictureFileKey );
            BlogPageTemplateHome.create( blogPageTemplate );
        }

        return   retrievedRequest;
    }

    /**
     * Add page template
     *
     * @param request
     * @return Json The Json succes or echec
     * @throws ParseException
     */
    public static String removePageTemplate( javax.servlet.http.HttpServletRequest request ) throws java.text.ParseException
    {
        // recovers portlet attributes

        String pageTemplaceName = request.getParameter( PARAMETER_PAGE_TEMPLATE_CODE );
        // create the multipart request
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
            org.apache.commons.fileupload.FileItem fileItem = multi.getFile( PARAMETER_PAGE_TEMPLATE_CODE );

        }
        // return the list of page template
        return "file Key";
    }

    /**
     * update page template
     *
     * @param request
     * @return Json The Json succes or echec
     * @throws ParseException
     */
    public static String updatePageTemplate( javax.servlet.http.HttpServletRequest request ) throws java.text.ParseException
    {
        // recovers portlet attributes

        String pageTemplaceName = request.getParameter( PARAMETER_PAGE_TEMPLATE_CODE );
        // create the multipart request
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
            org.apache.commons.fileupload.FileItem fileItem = multi.getFile( PARAMETER_PAGE_TEMPLATE_CODE );

        }
        // return the list of page template
        return "file Key";
    }
    protected String getPageUrl(int nIdPage) {
        return "../../site/AdminSite.jsp?page_id=" + nIdPage;
    }
}
