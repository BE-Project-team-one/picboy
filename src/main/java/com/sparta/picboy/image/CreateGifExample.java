package com.sparta.picboy.image;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class CreateGifExample {

    public String test2() throws IOException {
        BufferedImage first = ImageIO.read(new File("src/main/resources/duke.jpg"));
        ImageOutputStream output = new FileImageOutputStream(new File("src/main/resources/example.gif"));

        GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), 250, true);
        writer.writeToSequence(first);

        File[] images = new File[]{
                new File("src/main/resources/duke-image-watermarked.jpg"),
                new File("src/main/resources/duke.jpg"),
                new File("src/main/resources/duke-text-watermarked.jpg"),
        };

        for (File image : images) {
            BufferedImage next = ImageIO.read(image);
            writer.writeToSequence(next);
        }



        writer.close();
        output.close();

        return images.toString();

    }

    public CreateGifExample() throws IOException {
    }


}