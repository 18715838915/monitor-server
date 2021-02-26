package init.luoyu.monitorapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LuoYu
 * date 2021/2/26
 */
@RestController
public class TestController {


    @GetMapping(value = "/test")
    public String test(){
        return "Success";
    }

}
