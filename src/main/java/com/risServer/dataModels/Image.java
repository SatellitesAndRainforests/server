package com.risServer.dataModels;

import lombok.*;

import java.io.File;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    private Integer id;
    private int capture_id;
    private String fileURL;
    private byte[] imageFile;

    public Image(String fileURL) {
        this.fileURL = fileURL;
    }



}
