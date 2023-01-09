package com.risServer.dataAccess;

import com.risServer.dataModels.Image;
import com.risServer.dataModels.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {


    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {

        return new User(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("user_name"),
                resultSet.getString("password"),
                resultSet.getString("role")

        );


    }

}



