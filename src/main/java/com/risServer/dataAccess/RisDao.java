package com.risServer.dataAccess;

import com.risServer.dataModels.Capture;
import com.risServer.dataModels.Image;
import com.risServer.dataModels.User;

import java.util.List;
import java.util.Optional;

public interface RisDao {

    List<Capture> selectAllCaptures();
    int insertCapture(Capture capture);
    int deleteCapture(int id);
    Optional<Capture> selectCaptureByFields(Capture capture);
    Optional<Capture> selectCaptureById(int id);
    List<Image> selectImagesByCaptureId(int captureId);
    int updateCapture(Capture capture);

    Optional<User> findByUserName(String username);
    List<User> getUsers();
    int insertUser(User user);




}
