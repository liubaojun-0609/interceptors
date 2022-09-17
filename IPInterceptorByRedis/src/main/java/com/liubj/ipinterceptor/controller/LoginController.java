package com.liubj.ipinterceptor.controller;

import com.liubj.ipinterceptor.myannotations.AccessLimit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liubaojun
 * Date: 2022/9/17
 * Time: 18:28
 * Description: No Description
 */
@RestController()
@Validated
public class LoginController {

    @AccessLimit(times = 5,second = 60)
    @GetMapping(value = "/doLogin")
    public String doLogin(@NotBlank(message = "用户名不能为空") String username,
                          @NotBlank(message = "密码不能为空") String password){

        return "欢迎您：" + username + "，登录成功！";
    }
}
