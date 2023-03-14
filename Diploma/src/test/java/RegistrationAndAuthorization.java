import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class RegistrationAndAuthorization {
    private WebDriver driver;
    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(co);
        driver.manage().window().maximize();
        driver.navigate().to("http://intershop5.skillbox.ru/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }
    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots\\screenshot.png"));
        driver.quit();
    }


    //    В этом блоке данные для подстановки в поля ввода
    Date currentDate = new Date();
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
    String newLogin = "ts" + formatForDateNow.format(currentDate); // Данные для ввода логина и почты пользователя будут
    // изменяться в зависомости от даты, так можно будет тестировать регистрацию нового аккаунта один раз в день,
    // если требуется чаще, то тогда можно добавить цикл.


    private String testNewName = newLogin;
    private String testNewEmail = newLogin + "@test.ru";
    private String testPassword = "123456";
    private String actualLogin = "autotest1";
    private String actualPassword = "123456";
    private String wrongEmail = "@Test.ru";
    private String wrongPassword = "123";

    private final By loginButtonLocator = By.cssSelector(".account");
    private final By registerButtonLocator = By.cssSelector(".custom-register-button");
    private final By fieldNameRegisterLocator = By.id("reg_username");
    private final By fieldEmailRegisterLocator = By.id("reg_email");
    private final By fieldPasswordRegisterLocator = By.id("reg_password");
    private final By registerSubmitButtonLocator = By.cssSelector(".woocommerce-form-register__submit");
    private final By registerMessageLocator = By.cssSelector(".content-page > div");
    private final By userNameLocator = By.cssSelector(".user-name");
    private final By fieldLoginNameLocator = By.id("username");
    private final By fieldLoginPasswordLocator = By.id("password");
    private final By buttonLoginSubmitLocator = By.cssSelector(".woocommerce-form-login__submit");
    private final By errorLoginMessage = By.cssSelector(".woocommerce-error > li");
    private final By logoutButtonLocator = By.cssSelector(".logout");
    private final By forgotPasswordMessageLinkLocator = By.cssSelector(".woocommerce-error > li a");
    private final By forgotPasswordButtonLocator = By.cssSelector(".lost_password a");
    private final By forgotPasswordFieldLocator = By.id("user_login");
    private final By forgotPasswordSubmitButton = By.cssSelector(".button");
    private final By forgotPasswordMessageAlertLocator = By.xpath("//*[@role = 'alert']");
    private final By registerErrorMessageLocator = By.xpath("//*[@role = 'alert'] // li");


    @Test
    public void registerNewUserTest() {
        driver.findElement(loginButtonLocator).click();
        driver.findElement(registerButtonLocator).click();
        driver.findElement(fieldNameRegisterLocator).sendKeys(testNewName);
        driver.findElement(fieldEmailRegisterLocator).sendKeys(testNewEmail);
        driver.findElement(fieldPasswordRegisterLocator).sendKeys(testPassword);
        driver.findElement(registerSubmitButtonLocator).click();

        Assert.assertEquals("Имя пользователя не совпадает", driver.findElement(userNameLocator).getText(),
                testNewName);
        Assert.assertTrue("Не отображается сообщение об успешной регистрации",
                driver.findElement(registerMessageLocator).isDisplayed());
        driver.findElement(logoutButtonLocator).click();
    }
    @Test
    public void loginUserTest() {
        driver.findElement(loginButtonLocator).click();
        driver.findElement(fieldLoginNameLocator).sendKeys(actualLogin);
        driver.findElement(fieldLoginPasswordLocator).sendKeys(actualPassword);
        driver.findElement(buttonLoginSubmitLocator).click();

        Assert.assertEquals("Имя пользователя не совпадает с отображаемым на сайте",
                driver.findElement(userNameLocator).getText(), actualLogin);
        driver.findElement(logoutButtonLocator).click();
    }
    @Test
    public void errorLoginWrongPasswordTest() {
        driver.findElement(loginButtonLocator).click();
        driver.findElement(fieldLoginNameLocator).sendKeys(actualLogin);
        driver.findElement(fieldLoginPasswordLocator).sendKeys(wrongPassword);
        driver.findElement(buttonLoginSubmitLocator).click();

        Assert.assertTrue("Не отображается ошибка - Неверный пароль, а так же ссылка на восстановление пароля",
                driver.findElement(forgotPasswordMessageLinkLocator).isDisplayed());
    }
    @Test
    public void errorLoginWrongEmailTest() {
        driver.findElement(loginButtonLocator).click();
        driver.findElement(fieldLoginNameLocator).sendKeys(wrongEmail);
        driver.findElement(fieldLoginPasswordLocator).sendKeys(actualPassword);
        driver.findElement(buttonLoginSubmitLocator).click();

        Assert.assertTrue("Не отображается ошибка - неверное имя пользователя",
                driver.findElement(errorLoginMessage).isDisplayed());
    }

    @Test
    public void forgotPasswordTest() {
        driver.findElement(loginButtonLocator).click();
        driver.findElement(forgotPasswordButtonLocator).click();
        driver.findElement(forgotPasswordFieldLocator).sendKeys(actualLogin);
        driver.findElement(forgotPasswordSubmitButton).click();

        Assert.assertTrue("Не отобразилось сообщение об отправке нового пароля на почту",
                driver.findElement(forgotPasswordMessageAlertLocator).isDisplayed());
    }

    @Test
    public void errorRegistrationUsingExistingEmailTest() {
        String expectedResult = "Error: Учетная запись с такой почтой уже зарегистировавана. Пожалуйста авторизуйтесь.";

        driver.findElement(loginButtonLocator).click();
        driver.findElement(registerButtonLocator).click();
        driver.findElement(fieldNameRegisterLocator).sendKeys(actualLogin);
        driver.findElement(fieldEmailRegisterLocator).sendKeys(testNewEmail);
        driver.findElement(fieldPasswordRegisterLocator).sendKeys(actualPassword);
        driver.findElement(registerSubmitButtonLocator).click();

        Assert.assertEquals("Текст ошибки не совпадает",
                driver.findElement(registerErrorMessageLocator).getText(), expectedResult);

    }

}
