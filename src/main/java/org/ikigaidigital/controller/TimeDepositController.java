package org.ikigaidigital.controller;

import org.ikigaidigital.dto.ListResponse;
import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.service.TimeDepositService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time-deposits")
public class TimeDepositController {

    private final TimeDepositService timeDepositService;

    public TimeDepositController(TimeDepositService timeDepositService) {
        this.timeDepositService = timeDepositService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListResponse<TimeDepositResponse> getTimeDeposits() {
        return ListResponse.data(timeDepositService.getTimeDeposits()).build();
    }

    @PostMapping("/update-balances")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateTimeDepositBalances() {
        timeDepositService.updateAllTimeDepositBalances();
    }
}
