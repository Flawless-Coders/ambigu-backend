package com.flawlesscoders.ambigu.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    // Guardar archivo en GridFS
    public String saveFile(MultipartFile file) throws IOException {
        GridFSUploadOptions options = new GridFSUploadOptions();
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), options);
        return fileId.toString(); // Retorna el ID del archivo en GridFS
    }

    public Optional<GridFsResource> getFile(String id) {
    // Crear la consulta para buscar el archivo por su _id
    Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));

    // Buscar el archivo en GridFS
    GridFSFile gridFSFile = gridFsTemplate.findOne(query);

    // Abrir el flujo de descarga desde GridFS
    return Optional.of(new GridFsResource(gridFSFile, gridFSBucket.openDownloadStream(gridFSFile.getObjectId())));
    }
}

