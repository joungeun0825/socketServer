package org.example;

public class OrderListController {

    private final LoginService loginService;

    public OrderListController(){
        this.loginService = new LoginService();
    }

    public String getOrderList() {
        if (loginService.login() == 200) {
            return "Success";
        }
        return "Fail";
    }
}
