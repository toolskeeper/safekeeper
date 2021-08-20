package cn.safekeeper.simple.web;

import cn.safekeeper.core.SafeKeeper;
import cn.safekeeper.plugin.web.Result;
import cn.safekeeper.simple.web.vo.UserVO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * SafeKeeper默认会放行/login访问
 */
@RestController
public class SimpleController {

    private static final String name="admin";
    private static final String password="admin";

    @RequestMapping("/sayHello")
    public String sayHello(){
        return "Dear developers, welcome to the SafeKeeper world!";
    }

    @RequestMapping("/safeKeeper")
    public String safeKeeper(){
        return "Dear developers, You should want to log in after access!";
    }

    @RequestMapping("/login")
    public Result login(@RequestBody UserVO user, HttpServletRequest request){
        if(!StringUtils.isEmpty(user)){
            //默认登录登录的逻辑
            if(user.getUsername().equals(name) && user.getPassword().equals(password)){
                //登录成功后，植入到SafeKeeper框架就完成
                SafeKeeper.safeLogic(request.getHeader("loginType")).login(user.getId());
                return Result.succeedWith(
                        SafeKeeper.safeLogic(request.getHeader("loginType")).getTokenInfo()
                        ,1000,"登录成功");
            }
        }
        return Result.failedWith(null,1001,"用户名密码错误");
    }

}
