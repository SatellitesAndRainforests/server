package com.risServer.api;

import com.risServer.dataAccess.RisDao;
import com.risServer.dataModels.Capture;
import com.risServer.dataModels.Image;
import com.risServer.dataModels.User;
import com.risServer.exceptions.CaptureAlreadyInDb;
import com.risServer.exceptions.NotFoundException;
import com.sshtools.client.SessionChannelNG;
import com.sshtools.client.SshClient;
import com.sshtools.client.tasks.AbstractCommandTask;
import com.sshtools.common.publickey.InvalidPassphraseException;
import com.sshtools.common.ssh.SshException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class RisService {

    private final String filePath = "/home/ubuntu/client/src/assets/captures/";
    //private final String nightVisionCameraIP = "192.168.1.15";
    private final String nightVisionCameraIP = "10.3.141.1";

    private final RisDao risDao;
    public RisService(RisDao risDao) {
        this.risDao = risDao;
    }


    public List<Capture> getAllCaptures() {

        List<Capture> captures = risDao.selectAllCaptures();

        for (Capture capture : captures) {

            List<Image> images = risDao.selectImagesByCaptureId(capture.getId());

            capture.setImages( images );

        }

        return captures;

    }

    protected List<User> getAllUsers() {
         return risDao.getUsers();
    }



    public void deleteCapture(Integer id) {

        Optional<Capture> captureToDelete = risDao.selectCaptureById(id);

        captureToDelete.ifPresentOrElse(   (capture) -> {

                List<Image> images = risDao.selectImagesByCaptureId(capture.getId());

                int result = risDao.deleteCapture(id);

                if ( result != 1 ) throw new IllegalStateException("oops could not delete capture");

                deleteImages( images );

                },
                () -> { throw new NotFoundException(String.format("Capture with id %s not found", id)); });

    }


    private void deleteImages(List<Image> images) {

        for (Image i : images) {

            File myObj = new File( filePath + i.getFileURL() );
            if (myObj.delete()) {
                System.out.println("Deleted the file: " + myObj.getName());
            } else {
                System.out.println("Failed to delete the file.");
            }


        }

    }


    public Capture getCaptureById(int id) {

        Capture capture = risDao.selectCaptureById(id).orElseThrow(() -> new NotFoundException(String.format("Capture with id %s not found", id)));

        List<Image> images = risDao.selectImagesByCaptureId(id);
        capture.setImages(images);

        return capture;

    }

    public User getUserByName( String userName ) {

        User user = risDao.findByUserName(userName).orElseThrow( () -> new NotFoundException(String.format("User with user_name %s not found", userName)) );

        return user;

    }

    protected int insertUser(User user) {

        int result = 0;
        //TODO: check if user is allready in database;
        try {
            result = risDao.insertUser(user);
            if (result != 1) throw new IllegalStateException("oops something went wrong");
        } finally {
            return result;
        }


    }

    public void updateCapture(Capture capture) {

        //returns error response if not found
        Capture  c = getCaptureById(capture.getId());

        int result = risDao.updateCapture(capture);

        if (result != 1) throw new IllegalStateException("could not update capture ? " + capture);

    }

    public void deleteImage( String fileName ) {
        try {
            boolean result = Files.deleteIfExists(Paths.get(this.filePath + fileName));
            if (result) System.out.println("files: " + fileName + " deleted");
            else System.out.println("files: " + fileName + " not deleted");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveImage( String fileURL, MultipartFile image ) throws IOException {

        // TODO: create new dao for local images. (service layer is for business).
        // service aggregates data from several daos.
        // set unique filename

        byte[] bytes = image.getBytes();
        Path path = Paths.get( this.filePath + fileURL );
        Files.write(path, bytes);

    }

    public byte[] readImage( String fileURL ) {

        Path path = Paths.get( this.filePath + fileURL );
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean clientOnline() {

        try {

            InetAddress address = InetAddress.getByName( this.nightVisionCameraIP );
            boolean isOnline = address.isReachable(10000);     // 10 seconds - check with ping -

            System.out.println(" --- " + this.nightVisionCameraIP + ":online = " + isOnline );
            return isOnline;

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean sendCommandToNightVisionCamera(String command ) {

        //String sshCommand = "./startLiveStream.sh";
        //command = "touch 1.txt";

        try (SshClient sshClient = new SshClient(this.nightVisionCameraIP, 22, "pi", new File("/home/witch/.ssh/id_rsa") , "" )) {

            if (sshClient.isAuthenticated()) {

                System.out.println(" --- Java SshClient: Authentication OK");

                AbstractCommandTask task = new AbstractCommandTask(sshClient.getConnection(), command ) {
                    protected void onOpenSession(SessionChannelNG session) throws IOException {
                    }
                };

                System.out.println(" --- Java SshClient: Executing Command '" + command + "'");

                sshClient.runTask(task);

                System.out.println(" --- Java SshClient: Command Sent Successfully");
                return true;

                } else {

                    System.out.println(" --- Java SshClient: Authentication Fail");
                    return false;

                }

            } catch (NoRouteToHostException e) {
                System.out.println(" --- NoRouteToHostException: Is the night cam running ?");
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SshException e) {
                throw new RuntimeException(e);
            } catch (InvalidPassphraseException e) {
            throw new RuntimeException(e);
        }


    }





    private Capture createCaptureFromFileName( MultipartFile file ) {

        String filename = file.getOriginalFilename();
        String trimmed = filename.substring(0,filename.length() -4);
        String[] parts = trimmed.split("__");

        Capture capture = new Capture(
                0,
                parts[0] + "  " + parts[1],
                "unknown",
                "unidentified",
                "",
                    Float.parseFloat( parts[2].substring(0, parts[2].length() -1) ),
                    Float.parseFloat( parts[3].substring(0, parts[3].length() -1) ),
                    parts[4],
                0.0,
                0.0 );

        return capture;

    }



    public boolean saveCaptureFromNvCam(MultipartFile file) {

        MultipartFile[] image = new MultipartFile[] {file};
        Capture capture = createCaptureFromFileName(file);

        return addNewCapture(capture, image);

    }


    public boolean addNewCapture(Capture capture, MultipartFile[] images) {

        if ( images.length == 0 ) {
            System.err.println("capture must have at least 1 image");
            return false;
        }

        List<Image> captureImages = new ArrayList<Image>();

        for (MultipartFile image: images) {

            String currentTime = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date());
            String fileURL = "image_created:_" + currentTime + "__filename:_" + image.getOriginalFilename();
            captureImages.add( new Image( fileURL ) );

            try {
                this.saveImage( fileURL, image );
            } catch (IOException e) {
                System.err.println(" --- --- --- Could not save image to disk --- --- ---");
                return false;
            }

        }

        capture.setImages(captureImages);

        Optional<Capture> alreadyInDatabase = risDao.selectCaptureByFields(capture);

        if (!alreadyInDatabase.isEmpty()) {
            System.err.println("Capture " + alreadyInDatabase.get().toString() + " with the same time and lon/lat already exists in the database");
            return false;
        } else {
            int result = risDao.insertCapture(capture);
            if (result != 1) {
                System.err.println("Could not insertCapture to Database");
                return false;
            } else {
                return true;
            }
        }

    }




}



