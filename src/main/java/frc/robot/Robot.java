//Libraries url
//https://software-metadata.revrobotics.com/REVLib-2023.json
//https://maven.ctr-electronics.com/release/com/ctre/phoenix/Phoenix5-frc2023-latest.json

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class Robot extends TimedRobot {
    private WPI_TalonSRX m_motor0;
    private WPI_TalonSRX m_motor1;
    private CANSparkMax m_motor2;
    private WPI_TalonSRX m_motor3;
    private WPI_TalonSRX m_motor4;
    private CANSparkMax m_motor5;
    private MotorControllerGroup m_right;
    private MotorControllerGroup m_left;
    private DifferentialDrive m_drive;
    private Joystick joystick;
    private DoubleSolenoid m_Solenoid;
    private DigitalInput armSwitch;
    private DigitalInput lengthSwitch;
    private Compressor pcmCompressor;
    private double startTime;
    
    @Override
    public void robotInit() {
        // 初始化各個馬達控制器
        m_motor0 = new WPI_TalonSRX(0);
        m_motor1 = new WPI_TalonSRX(1);
        m_motor2 = new CANSparkMax(2, MotorType.kBrushless);
        m_motor3 = new WPI_TalonSRX(3);
        m_motor4 = new WPI_TalonSRX(4);
        m_motor5 = new CANSparkMax(8, MotorType.kBrushless);

        //創建極限開關
        armSwitch = new DigitalInput(0);
        lengthSwitch = new DigitalInput(1);

        // 初始化氣動裝置
        m_Solenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 6, 7);
        pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
        // 創建左右馬達組
        m_left = new MotorControllerGroup(m_motor4, m_motor0);
        m_right = new MotorControllerGroup(m_motor3, m_motor1);

        // 創建驅動
        m_drive = new DifferentialDrive(m_left, m_right);
        joystick = new Joystick(0);
    }

    @Override
    public void teleopPeriodic() {
        double turnSpeed = 0.5 * joystick.getRawAxis(0);
        double driveSpeed = 0.7 * joystick.getRawAxis(5);

        // 獲取觸發按鈕的值
        double leftTrigger = joystick.getRawAxis(2); // 假設2號軸是左觸發
        double rightTrigger = joystick.getRawAxis(3); // 假設3號軸是右觸發

        // double speed = 0.5;

        if(!armSwitch.get()){
            if (leftTrigger > 0.5) {
                // 如果左觸發按下超過一半，設定馬達2的速度為0.5
                m_motor2.set(0.5);
            }
            else if (rightTrigger > 0.5) {
                // 如果右觸發按下超過一半，設定馬達2的速度為-0.5
                m_motor2.set(-0.5);
            }
            else{
                // 如果兩個觸發都未按下，停止馬達2
                m_motor2.set(0);
            }
        }
        else if (leftTrigger > 0.5) {
            // 如果左觸發按下超過一半，設定馬達2的速度為0.5
            m_motor2.set(0.5);
        }
        else{
            m_motor2.set(0);
        }

        if(!lengthSwitch.get()){
            if(joystick.getRawButton(3)){
                m_motor5.set(0.5);
            }
            else if(joystick.getRawButton(2)){
                m_motor5.set(-0.5);
            }
            else{
                m_motor5.set(0);
            }
        }
        else if (joystick.getRawButton(2)){
            m_motor5.set(-0.5);
        }
        else{
            m_motor5.set(0);
        }

        // 檢查手把按鈕是否被按下
        if (joystick.getRawButton(5)) {
            // 如果手把按鈕2被按下，設定氣閥為正向動作（開啟）
            m_Solenoid.set(DoubleSolenoid.Value.kForward);
        } else if (joystick.getRawButton(6)) {
            // 如果手把按鈕3被按下，設定氣閥為反向動作（關閉）
            m_Solenoid.set(DoubleSolenoid.Value.kReverse);
        } else {
            // 如果手把按鈕2和按鈕3都沒有被按下，將氣閥設定為中立位置（Off）
            m_Solenoid.set(DoubleSolenoid.Value.kOff);
        }

        // 檢查手把按鈕是否被按下
        if (joystick.getRawButton(4)) {
            // 如果手把按鈕4被按下，啟動壓縮機，開始充氣
            pcmCompressor.enableDigital();
        } else {
            // 如果手把按鈕5未被按下，停止壓縮機，停止充氣
            pcmCompressor.disable();
        }

        // 使用搖桿模式驅動機器人
        m_drive.arcadeDrive(turnSpeed, driveSpeed);
    }

    @Override
    public void autonomousInit(){
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public void autonomousPeriodic(){
        double time = Timer.getFPGATimestamp();

        if(time - startTime < 5){
            m_motor1.set(-0.3);
            m_motor3.set(-0.3);
            m_motor0.set(0.3);
            m_motor4.set(0.3);
        }
        else {
            m_motor1.set(0);
            m_motor3.set(0);
            m_motor0.set(0);
            m_motor4.set(0);
            pcmCompressor.enableDigital();
        }
    }
}