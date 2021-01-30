package utilities;

import org.openqa.selenium.WebDriver;

public class WindowManager {

    private final WebDriver.Navigation navigate;

    public WindowManager(WebDriver driver) {
        navigate = driver.navigate();
    }

    public void goBack() {
        navigate.back();
    }

    public void goForward() {
        navigate.forward();
    }

    public void refreshPage() {
        navigate.refresh();
    }

    public void goToURL(String url) {
        navigate.to(url);
    }

}
