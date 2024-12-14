package december14Code;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TeleOpV3", group = "Drive")
public class teleopProgramV3 extends OpMode {
    // Declare motors
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor leftLiftMotor, rightLiftMotor;
    private Servo basketServo;
    private static final double FRONT_WHEEL_RATIO = 3.0 / 1.0; // 3:2 sprocket ratio
    public double motorSpeed = .5;

    boolean noBasketPosition = true;
    boolean lowBasket = false;
    boolean highBasket = false;

    @Override
    public void init() {
        // Initialize motors
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Set motors' directions if needed
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Set motors to use encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Servo for basket
        basketServo = hardwareMap.get(Servo.class, "basketServo");

        // Initialize lift motors
        leftLiftMotor = hardwareMap.get(DcMotor.class, "leftLiftMotor");
        rightLiftMotor = hardwareMap.get(DcMotor.class, "rightLiftMotor");

        // Set lift motors to use encoders
        leftLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set all motors to zero power
        setMotorPower(0, 0, 0, 0);
    }

    @Override
    public void loop() {
        // Get joystick values (replace gamepad with your controller source)
        double y = -gamepad1.left_stick_y; // Forward/backward
        double x = gamepad1.left_stick_x * 1.1; // Strafe
        double rotation = gamepad1.right_stick_x; // Rotate

        // Mecanum drive calculations
        double frontLeftPower = (y + x + rotation) * FRONT_WHEEL_RATIO;
        double frontRightPower = (y - x - rotation) * FRONT_WHEEL_RATIO;
        double backLeftPower = y - x + rotation;
        double backRightPower = y + x - rotation;

        // Normalize the power values to be within -1 and 1
        double maxPower = Math.max(1.0, Math.abs(frontLeftPower));
        frontLeftPower /= maxPower;
        frontRightPower /= maxPower;
        backLeftPower /= maxPower;
        backRightPower /= maxPower;

        // Set motor power
        setMotorPower(frontLeftPower, frontRightPower, backLeftPower, backRightPower);

        // Check for lift and basket inputs
        lift();
        basket();

        // Display encoder tick values for each lift motor
        telemetry.addData("Left Lift Motor Position:", leftLiftMotor.getCurrentPosition());
        telemetry.addData("Right Lift Motor Position:", rightLiftMotor.getCurrentPosition());
        telemetry.update();
    }

    public void basket() {
        boolean basketUp = gamepad1.dpad_up;
        boolean basketDown = gamepad1.dpad_down;

        if (basketUp) {
            basketServo.setPosition(.5);
        } else if (basketDown) {
            basketServo.setPosition(0.0); // Corrected position value
        }
    }

    public void preparelift(int leftTargetPosition, int rightTargetPosition) {
        // Strafing configuration for mecanum wheels
        leftLiftMotor.setTargetPosition(leftTargetPosition);
        rightLiftMotor.setTargetPosition(rightTargetPosition);
        leftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void lift() {
        boolean liftDown = gamepad1.right_bumper;
        boolean liftUp = gamepad1.left_bumper;

        if (liftUp) {
            if (noBasketPosition) {
                preparelift(-2243, 1075); // low basket encoder positions
                leftLiftMotor.setPower(motorSpeed);
                rightLiftMotor.setPower(-motorSpeed);

                lowBasket = true;
                noBasketPosition = false;
            }
            else if (lowBasket && !leftLiftMotor.isBusy() && !rightLiftMotor.isBusy()) {
                preparelift(-4953, 1580); // high basket encoder positions
                leftLiftMotor.setPower(motorSpeed);
                rightLiftMotor.setPower(-motorSpeed);

                highBasket = true;
                lowBasket = false;
            }
        } else if (liftDown) {
            if (lowBasket && !leftLiftMotor.isBusy() && !rightLiftMotor.isBusy()) {
                // decrease low basket encoder positions
                leftLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                rightLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                leftLiftMotor.setPower(-motorSpeed);
                rightLiftMotor.setPower(motorSpeed);

                lowBasket = false;
                noBasketPosition = true;
            }
            else if (highBasket) {
                preparelift(-2243, 1075);
                leftLiftMotor.setPower(-motorSpeed);
                rightLiftMotor.setPower(motorSpeed);

                highBasket = false;
                lowBasket = true;
            }
        } else {
            leftLiftMotor.setPower(0);
            rightLiftMotor.setPower(0);
        }
    }

    private void setMotorPower(double fl, double fr, double bl, double br) {
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }

    @Override
    public void stop() {
        setMotorPower(0, 0, 0, 0);
    }
}