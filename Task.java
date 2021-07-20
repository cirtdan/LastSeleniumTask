import com.github.javafaker.Faker;
import junit.framework.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Task {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "/Users/rafaelaziz/Documents/drivers/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // 1. Navigate to carmax.com
        driver.get("https://www.carmax.com/");

        // 2. On the bottom of the page in the appraisal form, choose VIN and fill out the form with
        //the below info and click get started: VIN: 4T1BE46K67U162207 Zipcode:22182
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,2000)");
        jse.executeScript("window.scrollBy(0,-800)");
        jse.executeScript("window.scrollBy(0,300)");
        driver.findElement(By.xpath("//button[@id='button-VIN']")).click();
        driver.findElement(By.id("ico-form-vin")).sendKeys("4T1BE46K67U162207", Keys.TAB, "22182", Keys.ENTER);

        // 3. On the next page, choose the following info:
        new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[.='LE 4D Sedan 2.4L']"))).click();

        new WebDriverWait(driver, 8).until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("drive"))));
        new Select(driver.findElement(By.name("drive"))).selectByValue("4WD/AWD");

        // 4. For features, check all options:
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        for (int i = 0; i < 12; i++) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].scrollIntoView(true);", checkboxes.get(i));
            checkboxes.get(i).click();
        }
        driver.findElement(By.xpath("//span[.='Mileage and condition']")).click();

        // 5. Enter the following mileage and the choose the following options:
        driver.findElement(By.name("currentMileage")).sendKeys("60000");

        List<WebElement> checkboxes2 = driver.findElements(By.xpath("//label[.='No']"));
        for (int i = 0; i <= 21; i++) {
            JavascriptExecutor executor2 = (JavascriptExecutor) driver;
            executor2.executeScript("arguments[0].scrollIntoView(true);", checkboxes2.get(i));
            if(i % 2 != 0)
            checkboxes2.get(i).click();
        }
        driver.findElement(By.xpath("//input[@id='radio-ico-r-600-1']")).click();
        driver.findElement(By.id("ico-continue-button")).click();

        // 6. Verify that Vehicle Information table contains the following expected data for the below 2 columns:
        Thread.sleep(5000);
        driver.findElement(By.id("ico-step-Vehicle_Profile-btn")).click();

        List<WebElement> carInfo = driver.findElements(By.xpath("//td//p[contains (@id, 'vehicleInfo')]"));
        List<String> actualCarInfo = new ArrayList<>();
        for (int i = 4; i < carInfo.size(); i++) {
            actualCarInfo.add(carInfo.get(i).getText());
        }
        List<String> expectedCarInfo = Arrays.asList("2007 Toyota Camry", "4WD/AWD", "Automatic", "4T1BE46K67U162207", "60,000");
        Assert.assertEquals(actualCarInfo, expectedCarInfo);

        // 7.Click continue // 8. On the next page, verify that the appraisal amount is 6600.
        String actualOffer = driver.findElement(By.xpath("//div[@class='kmx-ico-offer-offerinfo Offer-module__offerInfo--26dFt']")).getText();
        Assert.assertTrue(actualOffer.contains("7,000"));

        // 9. Click continue
        Thread.sleep(5000);
        driver.findElement(By.xpath("//button[.='Continue']")).click();

        // 10. On the next page which opens in new window, write a code that chooses one of the locations randomly:
        String winHandleBefore = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();

        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            if(driver.getTitle().equals("Appraisal Appointment | CarMax")){
                break;
            }
        }
        List<WebElement> locations = driver.findElements(By.xpath("//div[@class='mdc-select kmx-select']//option"));
        WebElement sortClick = driver.findElement(By.xpath("//select[@class='mdc-select__native-control']"));
        Select selectOption = new Select(sortClick);
        selectOption.selectByIndex((int) (Math.random() * locations.size()));

        // 11. Choose the first available date:
        driver.findElement(By.xpath("//input[@id='react-datepicker']")).click();
        WebElement firstAvailableDate = driver.findElement(By.xpath("//div[starts-with(@aria-label, 'Choose')]"));
        firstAvailableDate.click();

        // 12. Choose the first available time:

        driver.findElement(By.xpath("//input[@id='react-timepicker']")).click();
        WebElement firstAvailableTime = driver.findElement(By.xpath("//li[@class='react-datepicker__time-list-item ']"));
        firstAvailableTime.click();

        // 13. Click next
        driver.findElement(By.xpath("//button[.='next']")).click();

        // 14. On the next page, fill out the form with random info. You can use Faker library
        // or external data file from Mockaroo. DO NOT click on next afterwards since clicking
        // it will create an actual appraisal appointment and will occupy the actual time slot.
        Faker fakeData = new Faker();
        driver.findElement(By.xpath("//input[@id='fname']")).sendKeys(fakeData.name().firstName());
        driver.findElement(By.xpath("//input[@id='lname']")).sendKeys(fakeData.name().lastName());
        driver.findElement(By.xpath("//input[@id='email']")).sendKeys(fakeData.internet().emailAddress());
        driver.findElement(By.xpath("//input[@id='phone']")).sendKeys("2361231234");

        // 15. Click on Privacy policy link which opens the new tab and verify that the title is “Privacy Policy | CarMax”
        driver.findElement(By.xpath("//a[.='Privacy Policy']")).click();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            if (driver.getTitle().equals("Privacy Policy | CarMax")) {
                break;
            }
        }
        // 16. Go back to previous window with the offer amount and click on Save this offer
        driver.close();
        driver.switchTo().window(winHandleBefore);

        driver.findElement(By.xpath("//button[.='Save this offer']")).click();

        // 17. On the pop-up window add random email and click send my offer

        driver.findElement(By.xpath("//label[.='Preferred email']")).sendKeys(fakeData.internet().emailAddress());
        driver.findElement(By.xpath("//button[@id='ico-send-offer-email']")).click();

        // 18. End the session by closing down all the windows
        driver.quit();
    }
}

           // Alternative way:

//        //      4. For features, check all options:
//        List<WebElement> checkboxes = driver.findElements(By.xpath("//label[starts-with(@for, 'checkbox-ico-cb')]"));
//        for (WebElement checkbox : checkboxes) {
//            if (!checkbox.isSelected()) {
//                js.executeScript("arguments[0].click();", checkbox);
//            }
//        }