package fr.paris.lutece.plugins.blog.service;

import java.util.List;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.business.file.File;
import org.apache.commons.fileupload.FileItem;
import fr.paris.lutece.portal.service.util.AppLogService;
public class BlogFileService
{
    public static File getFileByKey( String strFileName ) throws Exception
    {
        String fileProviderName = AppPropertiesService.getProperty( "blog.FileProviderStore" );
        IFileStoreServiceProvider fileStoreServiceProvider = FileService.getInstance( ).getFileStoreServiceProvider(fileProviderName);
        return fileStoreServiceProvider.getFile( strFileName );
    }

    public static String getFileStoreProvideName(){
        return AppPropertiesService.getProperty( "blog.FileProviderStore" );
    }
    public static void deleteFile( String strFileName )
    {
        IFileStoreServiceProvider fileStoreServiceProvider = FileService.getInstance( ).getFileStoreServiceProvider(getFileStoreProvideName());
        String strFileError = "";
        try
        {
            if ( fileStoreServiceProvider.getFile( strFileName ) != null )
            {
                fileStoreServiceProvider.delete( strFileName );
            }
            else
            {
                strFileError = "The file does not exist in the file store";
            }
        }
        catch( Exception e )
        {
            AppLogService.error( strFileError, e );
        }
    }

    public void deleteFiles( List<String> listFiles ) throws Exception
    {
        IFileStoreServiceProvider fileStoreServiceProvider = FileService.getInstance( ).getFileStoreServiceProvider(getFileStoreProvideName());
        String strFileError = "";
        try
        {
            for ( String strFileName : listFiles )
            {
                if ( fileStoreServiceProvider.getFile( strFileName ) != null )
                {
                    fileStoreServiceProvider.delete( strFileName );
                }
                else
                {
                    strFileError = "The file does not exist in the file store";
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( strFileError, e );
        }
    }

    public static String storeFileItem( FileItem luteceFile ) throws Exception
    {
        IFileStoreServiceProvider fileStoreServiceProvider = FileService.getInstance( ).getFileStoreServiceProvider(getFileStoreProvideName());
        return fileStoreServiceProvider.storeFileItem( luteceFile );
    }
    public static String storeFile( File luteceFile ) throws Exception
    {
        IFileStoreServiceProvider fileStoreServiceProvider = FileService.getInstance( ).getFileStoreServiceProvider(getFileStoreProvideName());
         return    fileStoreServiceProvider.storeFile( luteceFile );

    }
}
