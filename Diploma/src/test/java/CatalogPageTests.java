
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;


import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class CatalogPageTests {
    private WebDriver driver;


    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(co);
        driver.manage().window().maximize();
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    }

    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("screenshots\\screenshot.png"));
        driver.quit();
    }

    private final By pageTitleLocator = By.xpath("//*[@class = 'ak-container'] // span");
    private final By nextPageButtonLocator = By.cssSelector(".next.page-numbers");
    private final By prevPageButtonLocator = By.cssSelector(".prev.page-numbers");
    private final By orderByFieldLocator = By.name("orderby");
    private final By firstItemPriceLocator = By.xpath("(//*[@class = 'products columns-4'] / li[1] // " +
            "*[@class = 'woocommerce-Price-amount amount'])[last()]");
    private final By filterPriceButtonLocator = By.cssSelector(".price_slider_amount .button");
    private final By sliderHandleLeftLocator = By.cssSelector(".price_slider .ui-slider-handle:nth-of-type(1)");
    private final By sliderHandleRightLocator = By.cssSelector(".price_slider .ui-slider-handle:nth-of-type(2)");
    private final By categoryTitleLocator = By.cssSelector(".entry-title");
    private final By firstItemTitleLocator = By.cssSelector(".columns-4 li:first-child h3");
    private final By firstItemLocator = By.cssSelector(".columns-4 li:first-child .inner-img a");
    private final By messageProductNotFound = By.cssSelector("#primary .woocommerce-info");
    private final By headerButtonBasketLocator = By.cssSelector(".menu-item-29 a");
    private final By nameItemInBasket = By.cssSelector("tbody .product-name");
    private final By priceItemInBasket = By.cssSelector("tbody .product-price bdi");
    private final By addToBasketButtonLocator = By.name("add-to-cart");
    private final By outOfStockItemLocator = By.cssSelector(".out-of-stock");
    private final By categoryWithoutItemButton = By.xpath("//*[@class = 'product-categories'] / *[4] /a");
    private final By category_Catalog_ButtonLocator = By.xpath("//*[@class = 'product-categories'] / *[3] /a");

    @Test
    public void paginationTest() {
        Integer pageNumber = 2;
        By pageNumbersButtonLocator = By.xpath("//ul[@class = 'page-numbers'] /li[" + pageNumber + "]");

        driver.findElement(pageNumbersButtonLocator).click();
        Assert.assertTrue("Номер выбранной страницы не совпадает с заголовком страницы. Выбор по номеру.",
                driver.findElement(pageTitleLocator).getText().contains(pageNumber.toString()));


        driver.findElement(nextPageButtonLocator).click();
        pageNumber++;
        Assert.assertTrue("Номер выбранной страницы не совпадает с заголовком страницы. Выбор через стрелку вперед",
                driver.findElement(pageTitleLocator).getText().contains(pageNumber.toString()));

        driver.findElement(prevPageButtonLocator).click();
        pageNumber--;
        Assert.assertTrue("Номер выбранной страницы не совпадает с заголовком страницы. Выбор через стрелку назад",
                driver.findElement(pageTitleLocator).getText().contains(pageNumber.toString()));
    }

    @Test
    public void sortingGoodsTest() {
        String expectedResult = "0,00₽";

        Select select = new Select(driver.findElement(orderByFieldLocator));
        select.selectByValue("price");

        Assert.assertEquals("Цена первого товара не совпадает с ожидаемым результатом",
                driver.findElement(firstItemPriceLocator).getText(), expectedResult);
    }

    @Test
    public void priceFilterTest() {
        String expectedResult = driver.findElement(firstItemPriceLocator).getText();
        Actions actions = new Actions(driver);

        actions.dragAndDropBy(driver.findElement(sliderHandleLeftLocator), 100, 0).perform();
        actions.dragAndDropBy(driver.findElement(sliderHandleRightLocator), -50, 0).perform();
        driver.findElement(filterPriceButtonLocator).click();

        Assert.assertNotEquals("Сортировка не выполнена", expectedResult, driver.findElement(firstItemPriceLocator).getText());
    }

    @Test
    public void transitionsByCategories() {
        String expectedResult;
        int numberOfCategory = 1;

        while (numberOfCategory < 13) {
            numberOfCategory++;
            By nameOfCategoryButton = By.xpath(String.format("//*[@class = 'product-categories'] / *[%s] /a", numberOfCategory));
            expectedResult = driver.findElement(nameOfCategoryButton).getText().toLowerCase();
            driver.findElement(nameOfCategoryButton).click();
            Assert.assertEquals("Заголовок категории не совпадает с названием в списке", expectedResult, driver.findElement(categoryTitleLocator).getText().toLowerCase());
        }
    }

    @Test
    public void addItemToCard() throws NoSuchElementException {
        String expectedResultTitle;
        String expectedResultPrice;
        int minNumberCategory = 1;
        int maxNumberCategory = 13;
        int numberOfCategory = minNumberCategory + (int) (Math.random() * maxNumberCategory);
        By nameOfCategoryButton = By.xpath(String.format("//*[@class = 'product-categories'] / *[%s] /a", numberOfCategory));
        driver.findElement(nameOfCategoryButton).click();


        try {
            expectedResultTitle = driver.findElement(firstItemTitleLocator).getText();
            expectedResultPrice = driver.findElement(firstItemPriceLocator).getText();
            driver.findElement(firstItemLocator).click();

            driver.findElement(addToBasketButtonLocator).click();
            driver.findElement(headerButtonBasketLocator).click();
            Assert.assertEquals("Название товара в корзине не совпадает с добавленным",
                    driver.findElement(nameItemInBasket).getText(), expectedResultTitle);
            Assert.assertEquals("Цена товара в корзине не совпадает с ценой в карточке товара",
                    driver.findElement(priceItemInBasket).getText(), expectedResultPrice);

        } catch (NoSuchElementException e) {
            System.out.println("нет товара на остатках или в выбранной категории товаров");
        }
    }

    @Test
    public void categoryWithoutProduct() {
        String expectedResult = "По вашему запросу товары не найдены.";

        driver.findElement(categoryWithoutItemButton).click();

        Assert.assertEquals("Не совпадает сообщение об отсутствии товаров в категории",
                driver.findElement(messageProductNotFound).getText(), expectedResult);
    }

    @Test
    public void outOfStockItem(){
        String expectedResult = "Out of stock";

        driver.findElement(category_Catalog_ButtonLocator).click();
        driver.findElement(firstItemLocator).click();

        Assert.assertEquals("Не совпадает сообщение об отсутствии товара на остатке",
                driver.findElement(outOfStockItemLocator).getText(), expectedResult);

    }
}

