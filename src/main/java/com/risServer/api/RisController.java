package com.risServer.api;

import com.risServer.dataModels.Capture;
import com.risServer.dataModels.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/captures/")
public class RisController {

    private final RisService risService;

    public RisController(RisService risService) {
        this.risService = risService;
    }

    //can add response entity wrapper 43:00 ? https://www.youtube.com/watch?v=Gx4iBLKLVHk&t=1257s&ab_channel=Amigoscode ?

    @GetMapping("find/every")
    public List<Capture> getAllCaptures() {
        return risService.getAllCaptures();
    }


    @GetMapping("find/{id}")
    public Capture getCaptureById(@PathVariable("id") Integer id) {
        return risService.getCaptureById(id);
    }


    @DeleteMapping("delete/{id}")
    public void deleteCapture(@PathVariable("id") Integer id) {
        risService.deleteCapture(id);
    }


    @PutMapping("update")
    public void updateCapture(@RequestBody Capture capture) {
        risService.updateCapture(capture);
    }



    @PostMapping(value = {"add-with-image"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Boolean> addNewWithImage(@RequestPart("capture") Capture capture,
                                                   @RequestPart("imageFiles") MultipartFile[] images) {
        boolean success = risService.addNewCapture(capture, images);
        return ResponseEntity.ok().body(success);
    }



    @PostMapping("image-from-client")
    public ResponseEntity<Boolean> uploadImage(@RequestParam("image") MultipartFile file) {

        boolean success = risService.saveCaptureFromNvCam(file);

        System.out.println("received file: " + file);
        System.out.println();

        if (success) return ResponseEntity.status(200).body(true);
        else return ResponseEntity.status(500).body(false);

    }








    @GetMapping("night-vision-camera")
    public boolean isOnline() {
        return risService.clientOnline();
    }

    @GetMapping("start-live-stream")
    public boolean startLiveStream() {
        return risService.sendCommandToNightVisionCamera( "sudo python3 ~/nightCam/programs/liveStream.py" );
    }

    @GetMapping("start-night-vision-automated-capturing")
    public boolean startNvCamCapturing() {
        return risService.sendCommandToNightVisionCamera("sudo python3 ~/nightCam/programs/nightWatch.py");
    }

    // Returns to idle used for stop livestream ...
    @GetMapping("stop-night-vision-camera")
    public boolean stopNvCamCapturing() {
        return risService.sendCommandToNightVisionCamera("sudo echo 'status:idle' > ~/nightCam/nvCamStatus.txt ");
    }

    @GetMapping("reboot-night-vision-camera")
    public boolean rebootNightVisionCamera() {
        return risService.sendCommandToNightVisionCamera("sudo reboot");
    }

    @GetMapping("retrieve-night-vision-camera-images")
    public boolean retrieveNightVisionCameraImages() {
        System.out.println("retrieveing night visoin camera trap images");
        return risService.sendCommandToNightVisionCamera("sudo python3 ~/nightCam/programs/sendAllImages.py");
    }


















 // --------------------- Users ------------------------------

    @GetMapping("find/user/{userName}")
    public ResponseEntity<User> getUserByName(@PathVariable("userName") String userName) {
        return ResponseEntity.ok().body(risService.getUserByName(userName));
    }

    @GetMapping("find/user/all")
    public ResponseEntity<List<User>> getAllUseres() {
        return ResponseEntity.ok().body(risService.getAllUsers());
    }


    @PostMapping("add-user")
    public ResponseEntity<Boolean> addUser(@RequestBody User user) {
        int result = risService.insertUser(user); // 0 = error, 1 = inserted
        if (result == 1) return ResponseEntity.ok().body(true);
        else return ResponseEntity.badRequest().body(false);
    }




}


