package com.risServer.dataAccess;

import com.risServer.dataModels.Capture;
import com.risServer.dataModels.Image;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageRowMapper implements RowMapper<Image> {

    @Override
    public Image mapRow(ResultSet resultSet, int i) throws SQLException {

        return new Image(
                resultSet.getInt("id"),
                resultSet.getInt("capture_id"),
                resultSet.getString("fileURL"),
                null
        );

    }

}