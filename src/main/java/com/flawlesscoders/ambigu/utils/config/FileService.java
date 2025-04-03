package com.flawlesscoders.ambigu.utils.config;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
    private GridFsOperations gridFsOperations; // Se usa para obtener GridFsResource

    // Guardar archivo en GridFS
    public String saveFile(MultipartFile file) {
        try {
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            return fileId.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en GridFS", e);
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