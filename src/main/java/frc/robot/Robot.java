package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DigitalInput;



public class Robot extends TimedRobot {
  private WPI_TalonSRX m_motor0;
  private WPI_TalonSRX m_motor1;
  private CANSparkMax m_motor2;
  private WPI_TalonSRX m_motor3;
  private WPI_TalonSRX m_motor4;
  private MotorControllerGroup m_right;
  private MotorControllerGroup m_left;
  private DifferentialDrive m_drive;
  private Joystick m_stick;
  private DigitalInput limitSwitch;
  

  @Override
  public void robotInit() {
    m_motor0 = new WPI_TalonSRX(0);
    m_motor1 = new WPI_TalonSRX(1);
    m_motor2 = new CANSparkMax(2, MotorType.kBrushless);
    m_motor3 = new WPI_TalonSRX(3);
    m_motor4 = new WPI_TalonSRX(4);

    limitSwitch = new DigitalInput(0);

    m_left = new MotorControllerGroup(m_motor4, m_motor0);
    m_right = new MotorControllerGroup(m_motor3, m_motor1);

    m_drive = new DifferentialDrive(m_left, m_right);
    m_stick = new Joystick(0);
  }

  @Override
public void teleopPeriodic() {
    double driveSpeed = -m_stick.getRawAxis(0);
    double turnSpeed = m_stick.getRawAxis(5);
    
    // Get the trigger axis values
    double leftTriggerValue = m_stick.getRawAxis(2);  // Assuming axis 2 is the left trigger
    double rightTriggerValue = m_stick.getRawAxis(3);  // Assuming axis 3 is the right trigger

    if(limitSwitch.get()){
      m_motor2.set(0);
    }
    else{
      if (leftTriggerValue > 0.5) {
        // If left trigger is pressed more than halfway, set motor2 speed to 0.5
        m_motor2.set(0.5);
      } else if (rightTriggerValue > 0.5) {
        // If right trigger is pressed more than halfway, set motor2 speed to -0.5
        m_motor2.set(-0.5);
      } else {
        // If neither trigger is pressed, stop motor2
        m_motor2.set(0.0);
      }
    }

    

    // Drive the robot using the arcade drive method
    m_drive.arcadeDrive(driveSpeed, turnSpeed);
}

  
}