package december14Code;

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "autoProgramV1")
public class autoProgramV1 extends OpMode {
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;

    final int CAMERA_WIDTH = 640;
    final int CAMERA_HEIGHT = 480;

    int timeLimit = 30;
    public int ticks = 28;
    public double motorSpeed = .5;

    int wheelDiameter = 75;
    public double wheelCircumference = Math.PI * wheelDiameter;

    AprilTagProcessor tagProcessor = new AprilTagProcessor.Builder().setDrawTagID(true).build();
    ElapsedTime runtime = new ElapsedTime();

    private static final double FRONT_WHEEL_RATIO = 3.0 / 1.0; // 3:2 sprocket ratio

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        VisionPortal visionPortal = new VisionPortal.Builder()
                .addProcessor(tagProcessor)
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                .build();
    }

    public void drive(int targetPosition, double motorSpeed) {
        frontLeft.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(motorSpeed);
        backLeft.setPower(motorSpeed);
        frontRight.setPower(motorSpeed);
        backRight.setPower(motorSpeed);
    }

    public void driveToAprilTag() {
        if (tagProcessor.getDetections().size() > 0) {
            AprilTagDetection currentTag = tagProcessor.getDetections().get(0);

            double tagDist = currentTag.ftcPose.z;
            telemetry.addData("z", tagDist);
            double aprilTagRotationNum = tagDist / wheelCircumference;
            double aprilTagPosition = ticks * aprilTagRotationNum;

            drive((int) aprilTagPosition, motorSpeed);
        }
        telemetry.update();
    }

    public void resetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void turn(int targetPosition, double motorSpeed) {
        frontLeft.setTargetPosition(-targetPosition);
        backLeft.setTargetPosition(-targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(motorSpeed);
        backLeft.setPower(motorSpeed);
        frontRight.setPower(motorSpeed);
        backRight.setPower(motorSpeed);
    }

    public void park() {
        double park_path_length = 589.05; // measured by meter stick, in mm
        double parkRotationNum = park_path_length / wheelCircumference;
        int parkLengthPosition = (int) (ticks * parkRotationNum);
        double turnPosition = ticks / 4; // 28 ticks = 1 rotation, so a fourth would equal a quarter

        drive(parkLengthPosition, motorSpeed);
        resetEncoders();
        turn((int) turnPosition, motorSpeed);
        driveToAprilTag();
        resetEncoders();
        turn((int) turnPosition, motorSpeed);
        drive(parkLengthPosition, motorSpeed);
    }

    public void strafePark() {
        double park_path_distance = 1500; // in mm
        double parkRotationNum = park_path_distance / wheelCircumference;
        int parkTickNum = (int) (ticks * parkRotationNum);

        strafe(parkTickNum, motorSpeed);
    }

    public void strafe(int targetPosition, double motorSpeed) {
        // Strafing configuration for mecanum wheels
        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(-targetPosition);
        backLeft.setTargetPosition(-targetPosition);
        backRight.setTargetPosition(targetPosition);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(motorSpeed);
        backLeft.setPower(motorSpeed);
        frontRight.setPower(motorSpeed);
        backRight.setPower(motorSpeed);
    }

    @Override
    public void loop() {
        strafePark();
    }
}
