import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;
import java.time.Duration;


public class HomePageTests {
    private WebDriver driver;
    private WebDriverWait wait;
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    }

    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots\\screenshot.png"));
        driver.quit();
    }


//     В этом блоке локаторы элементов
    private final By registerButtonLocator = By.cssSelector(".custom-register-button");
    private final By headerCatalogButtonLocator = By.id("menu-item-46");
    private final By catalogElement_Electric_Locator = By.id("menu-item-47");
    private final By catalogElement_Phones_Locator = By.id("menu-item-114");
    private final By elementTitle_Phones_Locator = By.xpath("//*[@class = 'woocommerce-breadcrumb accesspress-breadcrumb'] //span");
    private final By slideButtonPrevLocator = By.xpath("//*[@id = 'accesspress_store_product-2'] // a[@class = 'slick-prev']");
    private final By slideNewProductLocator = By.cssSelector("#accesspress_store_product-2");
    private final By elementSlideButtonLocator = By.xpath("//*[@id='accesspress_store_product-2']//li[14]//a//img");
    private final By elementSlidePriceLocator = By.xpath("//*[@id='accesspress_store_product-2']//li[14]//span/ins/span");
    private final By elementCardPriceLocator = By.cssSelector(".summary > .price ins bdi");
    private final By elementCardTitleLocator = By.xpath("//*[@class='product_title entry-title']");
    private final By nameFirstElementOfCatalogLocator = By.cssSelector(".product:first-child h3");
    private final By viewedGoodTitleLocator = By.cssSelector(".cat-list-wrap .product-title");
    private final By headerButtonBasketLocator = By.cssSelector(".menu-item-29 a");
    private final By searchFieldLocator = By.cssSelector(".search-field");
    private final By searchTitleLocator = By.cssSelector(".entry-title");
    private final By searchMessageLocator = By.cssSelector(".woocommerce-info");
    private final By promoCategoryCard = By.cssSelector("#accesspress_storemo-2 a");
    private final By promoCategoryCardTitle = By.cssSelector("#accesspress_storemo-2 h4");
    private final By fullPromoCard = By.cssSelector("#accesspress_store_full_promo-2 a");
    private final By fullPromoCardDescription = By.cssSelector("#accesspress_store_full_promo-2 .promo-desc-title");
    private final By stickerNewLocator = By.xpath("//*[@id = 'accesspress_store_product-3']//*[@data-slick-index = '0']//*[@class = 'label-new'] ");
    private final By headerMyAccountButtonLocator = By.cssSelector(".menu-item-30 a");
    private final By headerMainPageButtonLocator = By.cssSelector(".menu-item-26 a");
    private final By basketEmptyMessageLocator = By.cssSelector(".cart-empty");
    private final By productCategoriesLocator = By.cssSelector("#woocommerce_product_categories-2");



//    Блок с тестами

    @Test
    public void navigationOnHeader() {
        driver.findElement(headerMyAccountButtonLocator).click();
        Assert.assertTrue("Не отображается кнопка зарегистрировать при переходе на страницу мой аккаунт",
                driver.findElement(registerButtonLocator).isDisplayed());

        driver.findElement(headerButtonBasketLocator).click();
        Assert.assertTrue("Не отображается сообщение о пустой корзине при переходе на страницу корзины",
                driver.findElement(basketEmptyMessageLocator).isDisplayed());

        driver.findElement(headerCatalogButtonLocator).click();
        Assert.assertTrue("Не отображаются категории товаров при переходе в каталог",
                driver.findElement(productCategoriesLocator).isDisplayed());

        driver.findElement(headerMainPageButtonLocator).click();
        Assert.assertTrue("Не отображается карточка с промо товарами при переходе на главную страницу",
                driver.findElement(promoCategoryCard).isDisplayed());
    }


    @Test
    public void navigationCatalogTest() {
        String actualResult = "Телефоны";
        Actions builder = new Actions(driver);
        builder.moveToElement(driver.findElement(headerCatalogButtonLocator))
                .moveToElement(driver.findElement(catalogElement_Electric_Locator))
                .moveToElement(driver.findElement(catalogElement_Phones_Locator)).click();
        Action mouseoverAndClick = builder.build();
        mouseoverAndClick.perform();

        Assert.assertEquals("Категория каталога не совпала с выбранной в списке",
                driver.findElement(elementTitle_Phones_Locator).getText(),
                actualResult);
    }
    @Test
    public void sliderNavigationTest() {
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(slideNewProductLocator)).build().perform();
        String actualResult = "";

        driver.findElement(slideButtonPrevLocator).click();
        wait.until(ExpectedConditions.elementToBeClickable(elementSlideButtonLocator));
        actualResult = driver.findElement(elementSlidePriceLocator).getText();
        driver.findElement(elementSlideButtonLocator).click();

        Assert.assertTrue("Не открылась карточка товара",
                driver.findElement(elementCardTitleLocator).isDisplayed());
        Assert.assertEquals("Название товара не совпадает с карточкой в слайдере",
                driver.findElement(elementCardPriceLocator).getText(), actualResult);
    }

    @Test
    public void viewedGoodsTest() {
        String actualResult = "";
        driver.findElement(headerCatalogButtonLocator).click();
        actualResult = driver.findElement(nameFirstElementOfCatalogLocator).getText();
        driver.findElement(nameFirstElementOfCatalogLocator).click();

        Assert.assertEquals("Названия карточки в каталоге и на странице товара не совпадают",
                driver.findElement(elementCardTitleLocator ).getText(), actualResult);

        driver.navigate().to("http://intershop5.skillbox.ru/");

        Assert.assertEquals("Название просмотренного товара не совпадает",
                driver.findElement(viewedGoodTitleLocator).getText(), actualResult);
    }

    @Test
    public void searchItemTest() {
        Actions actions = new Actions(driver);
        String generalizedSearch = "телефон";
        String exactSearch = "OnePlus 8 Pro";
        String wrongSearch = "abrakadabra";
        String expectedResultWrongSearch = "По вашему запросу товары не найдены.";

        actions.moveToElement(driver.findElement(searchFieldLocator))
                .click()
                .sendKeys(generalizedSearch)
                .sendKeys(Keys.ENTER)
                .build()
                .perform();
        Assert.assertTrue("Результат поиска не совпадает с запросом",
                driver.findElement(searchTitleLocator).getText().toLowerCase().contains(generalizedSearch));

        driver.findElement(searchFieldLocator).clear();
        actions.moveToElement(driver.findElement(searchFieldLocator))
                .click()
                .sendKeys(exactSearch)
                .sendKeys(Keys.ENTER)
                .build()
                .perform();
        Assert.assertTrue("Страница товара не совпадает с поиском",
                driver.findElement(elementCardTitleLocator).getText().contains(exactSearch));

        driver.findElement(searchFieldLocator).clear();
        actions.moveToElement(driver.findElement(searchFieldLocator))
                .click()
                .sendKeys(wrongSearch)
                .sendKeys(Keys.ENTER)
                .build()
                .perform();
        Assert.assertTrue("Не отображается сообщение с результатами поиска",
                driver.findElement(searchMessageLocator).isDisplayed());
        Assert.assertEquals("Неверное сообщение о результатах поиска",
                driver.findElement(searchMessageLocator).getText(), expectedResultWrongSearch);
    }

    @Test
    public void promoGoodsTest(){
        String expectedResult = "";

        wait.until(ExpectedConditions.visibilityOfElementLocated(promoCategoryCardTitle));
        expectedResult = driver.findElement(promoCategoryCardTitle).getText();
        driver.findElement(promoCategoryCard).click();

        Assert.assertEquals("", expectedResult, driver.findElement(searchTitleLocator).getText());
    }

    @Test
    public void availableForSaleProductTest() {
        String expectedResult = "";

        expectedResult = driver.findElement(fullPromoCardDescription).getText();
        driver.findElement(fullPromoCard).click();

        Assert.assertTrue("Название товара не совпадает с названием в промо",
                driver.findElement(elementCardTitleLocator).getText().contains(expectedResult));
    }

    @Test
    public void newArrivalsStickerOnCard() {
        Assert.assertTrue("", driver.findElement(stickerNewLocator).isDisplayed());
    }

}
