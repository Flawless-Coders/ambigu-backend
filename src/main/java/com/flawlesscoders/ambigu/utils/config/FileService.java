package com.flawlesscoders.ambigu.utils.config;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    public String saveFile(MultipartFile file) {
        try {
            // Determinar el tipo de imagen
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            
            if (contentType == null) {
                contentType = determineContentTypeFromFilename(originalFilename);
            }

            // Procesar según el tipo de imagen
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg"))) {
                return saveCompressedJpeg(file, 0.8f); // 80% calidad para JPEG
            } else if (contentType != null && contentType.equals("image/png")) {
                return saveOptimizedPng(file);
            } else {
                // Guardar otros tipos sin procesamiento
                return saveRawFile(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo", e);
        }
    }

    private String saveCompressedJpeg(MultipartFile file, float quality) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Archivo no es una imagen válida");
        }

        ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
        
        // Configuración de compresión JPEG
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = writers.next();
        
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(compressedOutput)) {
            writer.setOutput(ios);
            
            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(quality);
            
            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }

        // Guardar en GridFS
        try (ByteArrayInputStream input = new ByteArrayInputStream(compressedOutput.toByteArray())) {
            String filename = replaceExtension(file.getOriginalFilename(), "jpg");
            ObjectId fileId = gridFsTemplate.store(input, filename, "image/jpeg");
            return fileId.toString();
        }
    }

    private String saveOptimizedPng(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Archivo no es una imagen PNG válida");
        }

        ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
        
        // Configuración de compresión PNG
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = writers.next();
        
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(compressedOutput)) {
            writer.setOutput(ios);
            
            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionType("Deflate");
            params.setCompressionQuality(0.9f); // Máxima compresión sin pérdida
            
            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }

        // Guardar en GridFS
        try (ByteArrayInputStream input = new ByteArrayInputStream(compressedOutput.toByteArray())) {
            ObjectId fileId = gridFsTemplate.store(input, file.getOriginalFilename(), "image/png");
            return fileId.toString();
        }
    }

    private String saveRawFile(MultipartFile file) throws IOException {
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return fileId.toString();
    }

    private String replaceExtension(String filename, String newExtension) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot != -1) {
            return filename.substring(0, lastDot) + "." + newExtension;
        }
        return filename + "." + newExtension;
    }

    private String determineContentTypeFromFilename(String filename) {
        if (filename == null) return null;
        
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (ext) {
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            default: return null;
        }
    }
    
    // Obtener archivo de GridFS
    public Optional<GridFsResource> getFile(String id) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            GridFSFile gridFSFile = gridFsTemplate.findOne(query);

            return Optional.of(gridFsOperations.getResource(gridFSFile));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el archivo de GridFS", e);
        }
    }

    // Eliminar archivo de GridFS
    public void deleteFile(String id) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            gridFsTemplate.delete(query);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el archivo de GridFS", e);
        }
    }

    public String saveFileFromBase64(String base64Data, String filename, String contentType) {
        try {
            byte[] data = java.util.Base64.getDecoder().decode(base64Data);
            return gridFsTemplate
                    .store(new java.io.ByteArrayInputStream(data), filename, contentType)
                    .toHexString();
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar archivo base64 en GridFS", e);
        }
    }
    
}