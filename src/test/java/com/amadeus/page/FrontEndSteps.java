package com.amadeus.page;

import com.thoughtworks.gauge.Step;
import com.amadeus.base.FrontEndTestBase;
import com.amadeus.model.ElementInfo;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FrontEndSteps extends FrontEndTestBase {

    List<String> tempDataList = new ArrayList<>();
    List<Integer> tempDataListInt = new ArrayList<>();

    String tempData;

    public static int DEFAULT_MAX_ITERATION_COUNT = 100;  // bir eylemin kaç kez tekrarlanacağını ifade eder
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 300; // her bir kontrol arasında bekleyeceğimiz süre
    /*
    static olarak tanımlamamızın sebebi sınıfın tüm örneklerin arasında paylaşılmasını ve değiştirilmesi gerektiği zaman tek bir yerden değiştirilmesini sağlamaktır.
     */

    public FrontEndSteps() throws IOException {
        String workingDir = System.getProperty("user.dir");
        initMap(getFileList(workingDir + "/src"));

    }

    public By getElementInfoBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        }
        return by;
    }

    WebElement findElement(String key) {

        By by = getElementInfoBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 20);
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})", element);
        return element;
    }

    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoBy(findElementInfoByKey(key)));
    }

    private void clickTo(WebElement element) {
        element.click();
    }

    private void sendKeysTo(WebElement element, String text) {
        element.sendKeys(text);
    }

    public void javaScriptClickTo(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);

    }

    @Step("<key> li elementi bul, temizle ve <text> değerini yaz, ardından Enter'a bas")
    public void sendKeysAndPressEnter(String key, String text) {
        WebElement element = findElement(key);
        element.clear();
        sendKeysTo(element, text);
        element.sendKeys(Keys.ENTER); // Elemente text değerini yazdıktan sonra Enter tuşuna bas
        logger.info("Element bulundu, yazıldı ve Enter'a basıldı: Key : " + key + " text : " + text);
    }

    @Step("Elementine tıkla <key>")
    public void clickElement(String key) {
        WebElement element = findElementWithWait(key);
        clickTo(element);
        logger.info(key + " elementine tıklandı.");
    }

    public WebElement findElementWithWait(String key) {
        WebDriverWait wait = new WebDriverWait(driver, 10); // Bekleme süresini örneğin 30 saniyeye çıkarın.
        By by = getElementInfoBy(findElementInfoByKey(key));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by)); // Elementin görünür olmasını bekleyin.
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        return element;
    }

    @Step("<int> saniye bekle")
    public void waitSecond(int seconds) throws InterruptedException {
        try {
            logger.info(seconds + " saniye bekleniyor");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("<key> elementinin disabled olduğunu kotrol et")
    public void checkDisabled(String key) {
        WebElement element = findElement(key);
        Assertions.assertTrue(element.isDisplayed(), " Element disabled değil");
        logger.info(key + " elementi disabled");
    }

    @Step("<key> elementinin <attribute> niteliği <value> değerine sahip mi")
    public void elementAttributeValueCheck(String key, String attribute, String value) throws InterruptedException {
        WebElement element = findElement(key);
        String actualValue;
        int count = 0;
        while (count < DEFAULT_MAX_ITERATION_COUNT) {
            actualValue = element.getAttribute(attribute).trim();
            if (actualValue.equals(value)) {
                logger.info(key + " elementinin " + attribute + " niteliği " + value + " değerine sahip.");
                return;
            }
            waitSecond(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(key + " elementinin " + attribute + " niteliği " + value + " değeri ile eşleşmiyor.");
    }

    @Step("<key> elementi <expectedText> değerini içeriyor mu kontrol et")
    public void checkElementEqualsText(String key, String expectedText) {

        String actualText = findElement(key).getText();
        logger.info("Element str:" + actualText);
        logger.info("Expected str:" + expectedText);
        Assertions.assertEquals(actualText, expectedText, "Beklenen metni içermiyor " + key);
        logger.info(key + " elementi " + expectedText + " degerine eşittir.");
    }

    @Step("<key> elementinin metnini kontrol et <expectedText>")
    public void checkElementText(String key, String expectedText) {
        WebElement element = findElement(key);
        String actualText = element.getText();
        Assertions.assertEquals(expectedText, actualText, key + " elementinin metni beklenen ile uyuşmuyor.");
        logger.info(key + " elementinin metni beklenen ile uyuşuyor: " + expectedText);
    }
    @Step("<foundItemsKey> elementindeki yazıda belirtilen uçuş sayısı ile gerçek uçuş sayısı <flightListKey> eşleşiyor mu kontrol et")
    public void checkFlightCountMatches(String foundItemsKey, String flightListKey) {
        // "Found X items" yazısını içeren elementi bul ve X değerini al
        WebElement foundItemsElement = findElement(foundItemsKey);
        String foundItemsText = foundItemsElement.getText();
        int expectedCount = Integer.parseInt(foundItemsText.replaceAll("\\D", "")); // Sayısal değeri almak için metinden rakamları çıkart

        // Gerçek uçuş listesini bul
        List<WebElement> flightList = findElements(flightListKey);
        int actualCount = flightList.size();

        // Log mesajını bas
        logger.info("Beklenen uçuş sayısı: " + expectedCount + ", Gerçek uçuş sayısı: " + actualCount);

        // Beklenen ve gerçek uçuş sayısını karşılaştır
        Assertions.assertEquals(expectedCount, actualCount, "Listelenen uçuş sayısı 'Found X items' yazısındaki ile uyuşmuyor.");
    }



}

                                     /*
                                     @Step anatosyonu Gauge kütüphanesine ait bir anatosyondur. bunun ile testlerimizde sürekli olarak çağırabileceğimiz
                                     cümlecikler halinde metodlar oluşturuyoruz.
                                     Bu sınıfı BaseTest sınıfı ile extends ediyoruz çünkü BaseTest sınıfında driver nesnesini oluşturuyoruz.
                                     BaseTest de olması gerekenler burada da olmalıdır.
                                     Extend ederek bir başka sınıfın özelliklerini miras alıp kullanabiliriz.

                                     bir sınıf başka bir sınıfı "extend" ettiğinde, temel alınan sınıfın ("superclass" veya "parent class" olarak adlandırılır) tüm halka açık metotları ve özellikleri, türetilen sınıfa ("subclass" veya "child class") aktarılır.
                                     Bu işlem sayesinde, kod tekrarını önlemek ve kodun yeniden kullanılabilirliğini artırmak mümkün olur.

                                     Polimorfizm: Alt sınıflar, üst sınıfın metodlarını kendi ihtiyaçlarına göre "override" edebilir (üzerine yazabilir),
                                     böylece aynı metot adı farklı sınıflarda farklı davranışlar sergileyebilir.
                                      */

