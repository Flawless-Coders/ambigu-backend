package com.flawlesscoders.ambigu.utils;

import java.util.List;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.flawlesscoders.ambigu.utils.config.FileService;

@Service
public class Base64ImageMigrationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FileService fileService;

    /**
     * @param entityClass Clase de la colección (ej. Dish.class o Category.class)
     * @param base64Field Nombre del campo con base64 (ej. "photoBase64")
     * @param photoIdField Nombre del campo donde se guardará el fileId (ej. "photoId")
     * @param filenameField Nombre del campo que se usará como nombre de imagen (ej. "name")
     */
    public <T> void migrate(
            Class<T> entityClass,
            String base64Field,
            String photoIdField,
            String filenameField
    ) {
        List<T> items = mongoTemplate.findAll(entityClass);
        for (T item : items) {
            try {
                String base64 = (String) new BeanWrapperImpl(item).getPropertyValue(base64Field);
                if (base64 != null && base64.startsWith("data:image")) {
                    String[] parts = base64.split(",");
                    String mimeType = parts[0].split(":")[1].split(";")[0];
                    String base64Data = parts[1];

                    String filename = (String) new BeanWrapperImpl(item).getPropertyValue(filenameField);
                    String fileId = fileService.saveFileFromBase64(base64Data, filename + ".jpg", mimeType);

                    BeanWrapperImpl wrapper = new BeanWrapperImpl(item);
                    wrapper.setPropertyValue(photoIdField, fileId);
                    wrapper.setPropertyValue(base64Field, null);

                    mongoTemplate.save(item);

                    System.out.println("✅ Migrado: " + filename);
                }
            } catch (Exception e) {
                System.err.println("❌ Error al migrar " + item + ": " + e.getMessage());
            }
        }

        System.out.println("🟢 Migración finalizada para " + entityClass.getSimpleName());
    }
}
