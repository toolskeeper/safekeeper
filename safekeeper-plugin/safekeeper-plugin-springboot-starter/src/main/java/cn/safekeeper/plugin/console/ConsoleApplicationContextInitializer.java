package cn.safekeeper.plugin.console;
import com.nepxion.banner.BannerConstant;
import com.nepxion.banner.Description;
import com.nepxion.banner.LogoBanner;
import com.nepxion.banner.NepxionBanner;
import com.taobao.text.Color;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.InputStream;

/**
 * banner打印
 * @author skylark
 */
public class ConsoleApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
            initialize("/logo.txt");
        }
    }
    protected void initialize(String resourceLocation) {
        InputStream inputStream = null;
        String bannerText = null;

        try {
            if (resourceLocation != null) {
                inputStream = ConsoleApplicationContextInitializer.class.getResourceAsStream(resourceLocation);
                bannerText = IOUtils.toString(inputStream, "UTF-8");
            }
        } catch (Exception ignored) {
        } finally {
            System.out.println(bannerText);
        }

    }



}
