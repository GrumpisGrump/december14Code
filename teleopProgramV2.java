package december14Code;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

// Declare the OpMode and set its name for the Driver Station
@TeleOp(name = "TeleOpV2", group = "Drive")
public class teleopProgramV2 extends LinearOpMode {

    // Declares drivetrain motors
    public DcMotor leftFront, leftRear, rightFront, rightRear;
    // Declares lift motors
    public DcMotor leftLiftMotor, rightLiftMotor;
    // Declares lift servo
    public Servo liftServo;
    // lift servo controls
    boolean liftUp = gamepad1.dpad_up;
    boolean liftDown = gamepad1.dpad_down;

//    // Declares Intake motors
//    public DcMotor intakeSlideMotor, intakeMotor;

    private void moveLift(int targetPosition) {
        boolean stopLift = gamepad1.x;

        while (!stopLift) {
            leftLiftMotor.setTargetPosition(targetPosition);
            rightLiftMotor.setTargetPosition(targetPosition);

            leftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    @Override
    public void runOpMode() {
        // Initialize the hardware variables
        initHardware();

        // Wait for the start button to be pressed
        waitForStart();

        // Main loop to control the robot
        while (opModeIsActive()) {
            // Get the joystick inputs from the gamepad
            double drive = -gamepad1.left_stick_y; // Forward/reverse
            double strafe = gamepad1.left_stick_x; // Strafing (left/right)
            double rotate = gamepad1.right_stick_x; // Rotation (turning)
            double Lift = gamepad1.right_stick_y; // Lift Up/Down
            double basketUp = gamepad1.right_trigger; // Basket Up
            double basketDown = gamepad1.left_trigger; // Basket Down
//            boolean intakeSlides = gamepad1.a; // Intake Extender/Retracted
//            boolean Intake = gamepad1.x; // Spin up/down intake

            // Compute the wheel powers based on mecanum drive kinematics
            double leftFrontPower = drive + strafe + rotate;
            double rightFrontPower = drive - strafe - rotate;
            double leftRearPower = drive - strafe + rotate;
            double rightRearPower = drive + strafe - rotate;

            // Normalize the wheel powers so no value exceeds 1
            double maxPower = Math.max(Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
                    Math.max(Math.abs(leftRearPower), Math.abs(rightRearPower)));

            if (maxPower > 1.0) {
                leftFrontPower /= maxPower;
                rightFrontPower /= maxPower;
                leftRearPower /= maxPower;
                rightRearPower /= maxPower;
            }

            // Sets the drivetrain motor powers
            leftFront.setPower(leftFrontPower);
            rightFront.setPower(rightFrontPower);
            leftRear.setPower(leftRearPower);
            rightRear.setPower(rightRearPower);

            if (Lift < 0) {
                moveLift(0);
            }

            if (liftUp) {
                liftServo.setPosition(.5);
            }
            else if (liftDown) {
                liftServo.setPosition(-.25);
            }

//            // Sets the lift up/down motor powers
//            leftLiftMotor.setPower(Lift);
//            rightLiftMotor.setPower(-Lift);
//
//            // Sets the basket up/down motor powers
//            leftLiftMotor.setPower(basketUp);
//            rightLiftMotor.setPower(basketUp);
//            leftLiftMotor.setPower(-basketDown);
//            rightLiftMotor.setPower(-basketDown);

            // Telemetry to debug motor power and joystick input
            telemetry.addData("Left Stick Y", gamepad1.left_stick_y);
            telemetry.addData("Left Stick X", gamepad1.left_stick_x);
            telemetry.addData("Right Stick X", gamepad1.right_stick_x);
            telemetry.addData("Right Trigger", gamepad1.right_trigger);
            telemetry.addData("Left Trigger", gamepad1.left_trigger);
            telemetry.addData("Dpad Down", gamepad1.dpad_down);
            telemetry.addData("Dpad Up", gamepad1.dpad_up);
            telemetry.addData("LF Power", leftFrontPower);
            telemetry.addData("RF Power", rightFrontPower);
            telemetry.addData("LR Power", leftRearPower);
            telemetry.addData("RR Power", rightRearPower);
            telemetry.addData("Slide Lift Power", Lift);
            telemetry.addData("Basket Up Power", basketUp);
            telemetry.addData("Basket Down Power", basketDown);
            telemetry.update();
        }
    }

    // Initialize motors and encoders
    private void initHardware() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        rightRear = hardwareMap.get(DcMotor.class, "rightRear");
        leftLiftMotor = hardwareMap.get(DcMotor.class, "leftLiftMotor");
        rightLiftMotor = hardwareMap.get(DcMotor.class, "rightLiftMotor");
        liftServo = hardwareMap.get(Servo.class, "liftServo");

        // Set motor directions (adjust based on your setup)
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftRear.setDirection(DcMotor.Direction.FORWARD);
        rightRear.setDirection(DcMotor.Direction.REVERSE);

        // Set motors to run using encoders
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // intakeSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
}