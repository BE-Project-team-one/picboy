package com.sparta.picboy.image;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


@Service
public class CreateGifExample {

    public String test2() throws IOException {
        BufferedImage first = ImageIO.read(new URL("https://doker-bucket.s3.ap-northeast-2.amazonaws.com/picboy/images/post85/6d48f5ec-b035-45e8-80bb-f66592956900-post85"));
        ImageOutputStream output = new FileImageOutputStream(new File("src/main/resources/example.gif"));

        GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), 250, true);
        writer.writeToSequence(first);

        URL[] images = new URL[]{
                new URL("https://doker-bucket.s3.ap-northeast-2.amazonaws.com/picboy/images/post62/565fa162-3ac9-4040-805a-d62e8f95df13-post62"),
                new URL("https://myblog-image.s3.ap-northeast-2.amazonaws.com/picboy/images/post63/e3b98d23-0e14-479f-b6a8-2c116f5bd29c-post63"),

        };

        for (URL image : images) {
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