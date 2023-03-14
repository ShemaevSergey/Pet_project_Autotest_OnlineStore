import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;

public class BasketTests {
    private WebDriver driver;
    private WebDriverWait wait;
    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(co);
        driver.manage().window().maximize();
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots\\screenshot.png"));
        driver.quit();
    }

    private final By headerButtonBasketLocator = By.id("menu-item-29");
    private final By emptyBasketMessageLocator = By.cssSelector(".cart-empty");
    private final By firstProductLocator = By.xpath("//*[@class = 'products columns-4'] // li[2] // *[@class = 'inner-img'] /a");
    private final By secondProductLocator = By.xpath("//*[@class = 'products columns-4'] // li[4] // *[@class = 'inner-img'] /a");
    private final By cardProductTitleLocator = By.cssSelector(".product_title");
    private final By cardProductPriceLocator = By.cssSelector(".summary .price bdi");
    private final By cardProductSalePriceLocator = By.cssSelector(".summary .price ins bdi");
    private final By cardProductButtonAddToBasketLocator = By.name("add-to-cart");
    private final By nameItemInBasket = By.cssSelector("tbody .product-name");
    private final By priceItemInBasket = By.cssSelector("tbody .product-price bdi");
    private final By headerCatalogButtonLocator = By.id("menu-item-46");
    private final By totalOrderSum = By.cssSelector(".order-total bdi");
    private final By productQuantityInBasketLocator = By.cssSelector(".input-text.qty");
    private final By productRemoveButtonInBasketLocator = By.cssSelector("td.product-remove a");
    private final By productReturnButtonAtBasketLocator = By.cssSelector(".woocommerce-message a");
    private final By couponFieldLocator = By.name("coupon_code");
    private final By applyCouponButtonLocator = By.name("apply_coupon");
    private final By checkoutButtonLocator = By.cssSelector(".checkout-button");
    private final By authorizedMessage = By.cssSelector(".showlogin");
    private final By updateBasketMessageLocator = By.xpath("//*[@role = 'alert']");
    private final By cardDiscountDeleteButtonLocator = By.cssSelector(".cart-discount a");

    @Test
    public void emptyBasketTest() {
        String expectedResult = "Корзина пуста.";
        driver.findElement(headerButtonBasketLocator).click();
        Assert.assertTrue("Не отображается сообщение о пустой корзине",
                driver.findElement(emptyBasketMessageLocator).isDisplayed());
        Assert.assertEquals("Не совпадает текст сообщения о пустой корзине",
                driver.findElement(emptyBasketMessageLocator).getText(), expectedResult);
    }

    @Test
    public void addProductToBasketTest() throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE); // меняю локаль т.к. в цене разделитель запятая а не точка

        String priceCard;
        String priceBasket;
        double expectedResultPrice;
        double actualResultPrice;
        String expectedResultTitle;

        driver.findElement(firstProductLocator).click();

        priceCard = driver.findElement(cardProductSalePriceLocator).getText();
        Number number = format.parse(priceCard);
        expectedResultPrice = number.doubleValue();

        expectedResultTitle = driver.findElement(cardProductTitleLocator).getText();

        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();

        priceBasket = driver.findElement(priceItemInBasket).getText();
        Number number1 = format.parse(priceBasket);
        actualResultPrice = number1.doubleValue();

        Assert.assertEquals("Название товара в корзине не совпадает с названием в карточке",
                driver.findElement(nameItemInBasket).getText(), expectedResultTitle);
        Assert.assertEquals("Стоимость товара в карточке не совпадает со стоимостью в корзине",
                actualResultPrice,expectedResultPrice, 0);
    }

    @Test
    public void sumProductsInBasketTest() throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        String firstProductPrice;
        String secondProductPrice;
        String sumOrder;
        double firstProductPriceValue;
        double secondProductPriceValue;
        double expectedResultSum;
        double sumOrderValue;

        driver.findElement(firstProductLocator).click();

        firstProductPrice = driver.findElement(cardProductSalePriceLocator).getText();
        Number number = format.parse(firstProductPrice);
        firstProductPriceValue = number.doubleValue();
        driver.findElement(cardProductButtonAddToBasketLocator).click();

        driver.findElement(headerCatalogButtonLocator).click();
        driver.findElement(secondProductLocator).click();

        secondProductPrice = driver.findElement(cardProductPriceLocator).getText();
        Number number1 = format.parse(secondProductPrice);
        secondProductPriceValue = number1.doubleValue();
        driver.findElement(cardProductButtonAddToBasketLocator).click();

        driver.findElement(headerButtonBasketLocator).click();

        expectedResultSum = firstProductPriceValue + secondProductPriceValue;
        sumOrder = driver.findElement(totalOrderSum).getText();
        Number number2 = format.parse(sumOrder);
        sumOrderValue = number2.doubleValue();

        Assert.assertEquals("Сумма товаров в корзине подсчитана с ошибкой",
                expectedResultSum, sumOrderValue, 0);
    }

    @Test
    public void increaseAmountGoodsInBasket () throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Actions actions = new Actions(driver);
        Integer productQuantity = 2;
        String priceCard;
        String priceBasket;
        double productPrice;
        double expectedResult;
        double actualResultPrice;

        driver.findElement(firstProductLocator).click();

        priceCard = driver.findElement(cardProductSalePriceLocator).getText();
        Number number = format.parse(priceCard);
        productPrice = number.doubleValue();

        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();

        actions.moveToElement(driver.findElement(productQuantityInBasketLocator))
                .click()
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(productQuantity.toString())
                .sendKeys(Keys.ENTER)
                .build()
                .perform();
        wait.until(ExpectedConditions.visibilityOfElementLocated(updateBasketMessageLocator));

        priceBasket = driver.findElement(totalOrderSum).getText();
        Number number1 = format.parse(priceBasket);

        actualResultPrice = number1.doubleValue();
        expectedResult = productPrice * productQuantity;

        Assert.assertEquals("Сумма всех товаров не равна ожидаемому результату",
                expectedResult,actualResultPrice, 0);
    }

    @Test
    public void deleteProductInBasket() {
        String expectedResult = "Корзина пуста.";

        driver.findElement(firstProductLocator).click();
        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();
        driver.findElement(productRemoveButtonInBasketLocator).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(emptyBasketMessageLocator));

        Assert.assertEquals("Не совпадает текст сообщения о пустой корзине",
                driver.findElement(emptyBasketMessageLocator).getText(), expectedResult);
    }

    @Test
    public void returnProductAtBasket() {
        String expectedResultTitle;

        driver.findElement(firstProductLocator).click();

        expectedResultTitle = driver.findElement(cardProductTitleLocator).getText();

        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();
        driver.findElement(productRemoveButtonInBasketLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(emptyBasketMessageLocator));
        driver.findElement(productReturnButtonAtBasketLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameItemInBasket));

        Assert.assertEquals("Название товара в корзине не совпадает с названием в карточке",
                driver.findElement(nameItemInBasket).getText(), expectedResultTitle);
    }
    @Test
    public void discountCouponTest() throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        String discountCoupon = "SERT500";
        double discountValue = 500;

        String priceCard;
        String priceBasket;
        double priceWithoutDiscount;
        double expectedResultPrice;
        double actualResultPrice;

        driver.findElement(firstProductLocator).click();

        priceCard = driver.findElement(cardProductSalePriceLocator).getText();
        Number number = format.parse(priceCard);
        priceWithoutDiscount = number.doubleValue();

        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();

        driver.findElement(couponFieldLocator).sendKeys(discountCoupon);
        driver.findElement(applyCouponButtonLocator).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(cardDiscountDeleteButtonLocator));

        priceBasket = driver.findElement(totalOrderSum).getText();
        Number number1 = format.parse(priceBasket);
        actualResultPrice = number1.doubleValue();

        expectedResultPrice = priceWithoutDiscount - discountValue;

        Assert.assertEquals("Сумма товара не равна ожидаемому результату",
                actualResultPrice, expectedResultPrice, 0);
    }

    @Test
    public void checkoutNonAuthorizedUserTest() {
        String expectedMessage = "Авторизуйтесь";

        driver.findElement(firstProductLocator).click();
        driver.findElement(cardProductButtonAddToBasketLocator).click();
        driver.findElement(headerButtonBasketLocator).click();
        driver.findElement(checkoutButtonLocator).click();

        Assert.assertTrue("Не отображается сообщение об необходимости авторизоваться на сайте",
                driver.findElement(authorizedMessage).isDisplayed());
        Assert.assertEquals("Текст сообщения об авторизации не совпадает с требуемым",
                expectedMessage, driver.findElement(authorizedMessage).getText());
    }

}
