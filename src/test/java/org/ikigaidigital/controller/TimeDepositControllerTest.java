package org.ikigaidigital.controller;

import org.ikigaidigital.dto.ListResponse;
import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.service.TimeDepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TimeDepositControllerTest {

    private TimeDepositService timeDepositServiceMock;
    private TimeDepositController target;

    @BeforeEach
    void setUp() {
        this.timeDepositServiceMock = mock(TimeDepositService.class);
        this.target = new TimeDepositController(timeDepositServiceMock);
    }

    @Test
    @DisplayName("Should return a list of time deposits")
    void getTimeDeposits() {
        var timeDepositResponse = TimeDepositResponse.aBuilder()
                .withId(1)
                .withPlanType(TimeDepositResponse.PlanType.BASIC)
                .withBalance(new BigDecimal("1000.00"))
                .withDays(30)
                .withWithdrawals(List.of())
                .build();
        var timeDeposits = Collections.singletonList(timeDepositResponse);
        var expectedResponse = ListResponse.data(timeDeposits).build();

        when(timeDepositServiceMock.getTimeDeposits()).thenReturn(timeDeposits);

        var actualResponse = target.getTimeDeposits();

        verify(timeDepositServiceMock, times(1)).getTimeDeposits();
        verifyNoMoreInteractions(timeDepositServiceMock);

        assertEquals(expectedResponse.data().size(), actualResponse.data().size());
    }

    @Test
    @DisplayName("Should update all time deposit balances")
    void updateAllTimeDepositBalances() {

        target.updateTimeDepositBalances();

        verify(timeDepositServiceMock).updateAllTimeDepositBalances();
        verifyNoMoreInteractions(timeDepositServiceMock);
    }
}