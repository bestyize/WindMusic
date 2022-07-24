package xyz.thewind.music;

import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UploadTest {
    @Test
    public void testUUID() {
        String fileName = "92021iri.png";
        String saveName = UUID.randomUUID().toString();
        if (fileName.contains(".")) {
            saveName += fileName.substring(fileName.lastIndexOf("."));
        }
        System.out.println(saveName);
    }
}
