package org.jeecg.handler.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * swagger聚合接口，三个接口都是 doc.html需要访问的接口
 * @author zyf
 * @date: 2022/4/21 10:55
 */
@RestController
@RequestMapping("/swagger-resources")
public class SwaggerResourceController {
    private MySwaggerResourceProvider swaggerResourceProvider;

    @Autowired
    private ApplicationContext applicationContext;
    // 生产环境profile配置模型
    private static final String PRODUCTION_PROFILE_NAME = "prod*";

    @Autowired
    public SwaggerResourceController(MySwaggerResourceProvider swaggerResourceProvider) {
        this.swaggerResourceProvider = swaggerResourceProvider;
    }

    @RequestMapping(value = "/configuration/security")
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<>(SecurityConfigurationBuilder.builder().build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/configuration/ui")
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<>(UiConfigurationBuilder.builder().build(), HttpStatus.OK);
    }

    @RequestMapping
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        // 如果激活的profile带有生产环境的profile，则屏蔽swagger资源
        if (isProd()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return new ResponseEntity<>(swaggerResourceProvider.get(), HttpStatus.OK);
    }

    private boolean isProd() {
        String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
        Pattern pattern = Pattern.compile(PRODUCTION_PROFILE_NAME);
        for (String profile : profiles) {
            if (pattern.matcher(profile).find()) {
                return true;
            }
        }

        return false;
    }
}