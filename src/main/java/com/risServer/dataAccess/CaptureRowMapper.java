package com.risServer.dataAccess;

import com.risServer.dataModels.Capture;
import com.risServer.dataModels.Image;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CaptureRowMapper implements RowMapper<Capture> {

    @Override
    public Capture mapRow(ResultSet resultSet, int i) throws SQLException {

            return new Capture(
                    resultSet.getInt("id"),
                    resultSet.getString("epoch_time"),
                    resultSet.getString("species"),
                    resultSet.getString("id_status"),
                    resultSet.getString("notes"),
                    resultSet.getFloat("temperature"),
                    resultSet.getFloat("humidity"),
                    resultSet.getString("moon_phase"),
                    resultSet.getDouble("longitude"),
                    resultSet.getDouble("latitude")

            );

    }

}
