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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class OrderingTests {
    private WebDriver driver;
    private WebDriverWait wait;


    Date currentDate = new Date();
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("MM.dd.mm.ss");
    String newLogin = "ts" + formatForDateNow.format(currentDate);
    private String testNewName = newLogin;
    private String testNewEmail = newLogin + "@ts.ru";
    private String testPassword = "123456";
    private String clientName = "Сергей";
    private String clientSurname = "Петров";
    private String clientAddress = "ул.Вязовая, д.13, кв. 9";
    private String clientCity = "Самара";
    private String clientRegion = "Самарская";
    private String clientZIPCode = "443000";
    private String clientPhone = "+79999999999";
    private String clientComment = "Просьба доставить целым";
    private String SuccessfulOrderMessage = "Заказ получен";
    private String discountCoupon = "SERT500";
    private final By loginButtonLocator = By.cssSelector(".account");

    private final By registerButtonLocator = By.cssSelector(".custom-register-button");
    private final By fieldNameRegisterLocator = By.id("reg_username");
    private final By fieldEmailRegisterLocator = By.id("reg_email");
    private final By fieldPasswordRegisterLocator = By.id("reg_password");
    private final By registerSubmitButtonLocator = By.cssSelector(".woocommerce-form-register__submit");
    private final By headerCatalogButtonLocator = By.id("menu-item-46");
    private final By firstProductLocator = By.xpath("//*[@class = 'products columns-4'] // li[2] // *[@class = 'inner-img'] /a");
    private final By cardProductButtonAddToBasketLocator = By.name("add-to-cart");
    private final By headerButtonBasketLocator = By.id("menu-item-29");
    private final By checkoutButtonLocator = By.cssSelector(".checkout-button");
    private final By nameFieldLocator = By.id("billing_first_name");
    private final By surnameFieldLocator = By.id("billing_last_name");
    private final By addressFieldLocator = By.id("billing_address_1");
    private final By cityFieldLocator = By.id("billing_city");
    private final By regionFieldLocator = By.id("billing_state");
    private final By zipCodeFieldLocator = By.id("billing_postcode");
    private final By phoneFieldLocator = By.id("billing_phone");
    private final By commentFieldLocator = By.id("order_comments");
    private final By emailFieldLocator = By.id("billing_email");
    private final By countrySelectFieldLocator = By.id("billing_country");
    private final By payMethodBankLocator = By.id("payment_method_bacs");
    private final By payMethodCashLocator = By.id("payment_method_cod");
    private final By orderSubmitButtonLocator = By.id("place_order");
    private final By orderSuccessfulMessageLocator = By.cssSelector(".post-title");
    private final By addCouponButtonLocator = By.cssSelector(".showcoupon");
    private final By addCouponFieldLocator = By.id("coupon_code");
    private final By addCouponApplyButtonLocator = By.name("apply_coupon");
    private final By alertCouponMessageLocator = By.xpath("//*[@role = 'alert']");
    private final By alertFieldEmptyMessageLocator = By.xpath("//*[@role = 'alert'] // li // strong");
    private final By accountUserHeaderButtonLocator = By.id("menu-item-30");
    private final By accountUserOrdersButtonLocator = By.cssSelector(".woocommerce-MyAccount-navigation-link--orders a");
    private final By ordersNumberInAccountUserLocator = By.cssSelector(".woocommerce-orders-table__cell-order-number a");


    @Before
    public void setUp()
    {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(co);

        driver.manage().window().maximize();
        driver.navigate().to("http://intershop5.skillbox.ru/");

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.findElement(loginButtonLocator).click();
        driver.findElement(registerButtonLocator).click();
        driver.findElement(fieldNameRegisterLocator).sendKeys(testNewName);
        driver.findElement(fieldEmailRegisterLocator).sendKeys(testNewEmail);
        driver.findElement(fieldPasswordRegisterLocator).sendKeys(testPassword);
        driver.findElement(registerSubmitButtonLocator).click();

        driver.findElement(headerCatalogButtonLocator).click();
        driver.findElement(firstProductLocator).click();
        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();
        driver.findElement(checkoutButtonLocator).click();

        driver.findElement(nameFieldLocator).sendKeys(clientName);
        driver.findElement(surnameFieldLocator).sendKeys(clientSurname);
        driver.findElement(addressFieldLocator).sendKeys(clientAddress);
        driver.findElement(cityFieldLocator).sendKeys(clientCity);
        driver.findElement(regionFieldLocator).sendKeys(clientRegion);
        driver.findElement(zipCodeFieldLocator).sendKeys(clientZIPCode);
        driver.findElement(phoneFieldLocator).sendKeys(clientPhone);
        driver.findElement(commentFieldLocator).sendKeys(clientComment);

    }

    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots\\screenshot.png"));
        driver.quit();
    }



    @Test
    public void placementOfOrderPositiveTest() throws InterruptedException {
        driver.findElement(payMethodBankLocator).click();
        driver.findElement(orderSubmitButtonLocator).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(orderSuccessfulMessageLocator, "Заказ получен"));

        Assert.assertEquals("Текст сообщения не совпадает с ожидаемым",
                driver.findElement(orderSuccessfulMessageLocator).getText(), SuccessfulOrderMessage);
    }
    @Test
    public void placementOfOrderAndAddDiscountCouponPositiveTest() {
        String expectedResult = "Купон успешно добавлен.";

        driver.findElement(payMethodBankLocator).click();
        driver.findElement(addCouponButtonLocator).click();
        driver.findElement(addCouponFieldLocator).sendKeys(discountCoupon);
        driver.findElement(addCouponApplyButtonLocator).click();


        Assert.assertTrue("Не появляется сообщение о добавлении купона",
                driver.findElement(alertCouponMessageLocator).isDisplayed());
        Assert.assertEquals("Текст сообщения не совпадает с ожидаемым",
                driver.findElement(alertCouponMessageLocator).getText(), expectedResult);

    }

    @Test
    public void placementOfOrderNegativeTest_EmptyFieldName() {
        String expectedResult = "Имя для выставления счета";

        driver.findElement(nameFieldLocator).clear();
        driver.findElement(orderSubmitButtonLocator).click();

        Assert.assertTrue("Не появляется сообщение об обязательности заполнения поля",
                driver.findElement(alertFieldEmptyMessageLocator).isDisplayed());
        Assert.assertEquals("Название поля не сопвадает с ожидаемым результатом",
                driver.findElement(alertFieldEmptyMessageLocator).getText(), expectedResult);
    }

    @Test
    public void placementOfOrderNegativeTest_EmptyFieldAddress() {
        String expectedResult = "Адрес для выставления счета";

        driver.findElement(addressFieldLocator).clear();
        driver.findElement(orderSubmitButtonLocator).click();

        Assert.assertTrue("Не появляется сообщение об обязательности заполнения поля",
                driver.findElement(alertFieldEmptyMessageLocator).isDisplayed());
        Assert.assertEquals("Название поля не сопвадает с ожидаемым результатом",
                driver.findElement(alertFieldEmptyMessageLocator).getText(), expectedResult);
    }

    @Test
    public void placementOfOrderNegativeTest_EmptyField() {
        String expectedResult = "Адрес почты для выставления счета";

        driver.findElement(emailFieldLocator).clear();
        driver.findElement(orderSubmitButtonLocator).click();

        Assert.assertTrue("Не появляется сообщение об обязательности заполнения поля",
                driver.findElement(alertFieldEmptyMessageLocator).isDisplayed());
        Assert.assertEquals("Название поля не сопвадает с ожидаемым результатом",
                driver.findElement(alertFieldEmptyMessageLocator).getText(), expectedResult);
    }
    @Test
    public void placementOfOrderNegativeTest_EmptyFieldZIPCode() {
        String expectedResult = "Почтовый индекс для выставления счета";

        driver.findElement(zipCodeFieldLocator).clear();
        driver.findElement(orderSubmitButtonLocator).click();

        Assert.assertTrue("Не появляется сообщение об обязательности заполнения поля",
                driver.findElement(alertFieldEmptyMessageLocator).isDisplayed());
        Assert.assertEquals("Название поля не сопвадает с ожидаемым результатом",
                driver.findElement(alertFieldEmptyMessageLocator).getText(), expectedResult);
    }
    @Test
    public void placementOfOrderNegativeTest_EmptyFieldPhone() {
        String expectedResult = "Телефон для выставления счета";

        driver.findElement(phoneFieldLocator).clear();
        driver.findElement(orderSubmitButtonLocator).click();

        Assert.assertTrue("Не появляется сообщение об обязательности заполнения поля",
                driver.findElement(alertFieldEmptyMessageLocator).isDisplayed());
        Assert.assertEquals("Название поля не сопвадает с ожидаемым результатом",
                driver.findElement(alertFieldEmptyMessageLocator).getText(), expectedResult);
    }

    @Test
    public void placementOfOrderPositiveAndCheckOrderInAccountUser () throws InterruptedException {
        driver.findElement(orderSubmitButtonLocator).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(orderSuccessfulMessageLocator, "Заказ получен"));

        driver.findElement(accountUserHeaderButtonLocator).click();


        driver.findElement(accountUserOrdersButtonLocator).click();


        Assert.assertTrue("", driver.findElement(ordersNumberInAccountUserLocator).isDisplayed());



    }







}
