package com.emailProcessor.emailProcessor.serializer;
import com.emailProcessor.basedomains.dto.CategoryDto;
import org.springframework.core.serializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;


public class CategoryDtoKeyDeserializer implements Deserializer<CategoryDto> {

    @Override
    public CategoryDto deserialize(InputStream inputStream) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            return (CategoryDto) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error deserializing CategoryDto", e);
        }
    }
}