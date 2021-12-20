package by.musicwaves.service.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class UploadableResourcesWorker {

    private static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger(UploadableResourcesWorker.class);
    }

    public static String uploadFile(UploadableResource resource, HttpServletRequest request, int id) throws IOException, ServletException {
        List<Part> parts = request.getParts().stream().collect(Collectors.toList());
        if (parts.isEmpty()) {
            // there is no file
            return null;
        }

        LOGGER.debug("Uploading file, found " + parts.size() + " parts");

        String originalFileName = getFileName(parts);

        String newFileName = generateFileName(originalFileName, id);
        // constructs path of the directory to save uploaded file
        File fileToBeSaved = new File(resource.getPathToResourceDirectory() + File.separator + newFileName);

        LOGGER.debug("new file path is: " + fileToBeSaved.getAbsolutePath());

        // creates upload folder if it does not exists
        File uploadFolder = new File(resource.getPathToResourceDirectory());
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // write file in folder
        for (Part part : request.getParts()) {
            part.write(fileToBeSaved.getAbsolutePath());
        }

        return fileToBeSaved.exists() ? newFileName : null;

    }

    private static String generateFileName(String fileName, int id) {
        String extension = getFileNameExtension(fileName);
        StringBuilder newFileName = new StringBuilder();
        newFileName.append(Integer.hashCode(id))
                .append("T")
                .append(System.currentTimeMillis());
        if (extension != null && !extension.equals("")) {
            newFileName.append(".")
                    .append(extension);
        }

        return newFileName.toString();
    }

    public static void deleteFile(UploadableResource resource, String fileName) {
        File oldFile = new File(resource.getPathToResourceDirectory() + File.separator + fileName);
        LOGGER.debug("trying to delete old file");
        LOGGER.debug("filepath is: " + oldFile.getAbsolutePath());
        LOGGER.debug("file exists: " + (oldFile.exists() && oldFile.isFile()));

        if (oldFile.exists() && oldFile.isFile()) {
            try {
                Files.delete(oldFile.toPath());
            } catch (IOException | SecurityException ex) {
                LOGGER.debug("Failed to delete file right away", ex);
                oldFile.deleteOnExit();
            }
        }
    }

    private static String getFileName(Iterable<Part> parts) {
        for (Part part : parts) {
            for (String content : part.getHeader("content-disposition").split(";")) {
                if (content.trim().startsWith("filename"))
                    return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }

    private static String getFileNameExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        String[] parts = fileName.trim().split("[.]");
        if (parts.length == 1) {
            // there is no extension
            return "";
        } else {
            return parts[parts.length - 1];
        }
    }
}
