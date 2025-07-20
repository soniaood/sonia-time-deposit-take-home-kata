package org.ikigaidigital.service;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.mapper.TimeDepositMapper;
import org.ikigaidigital.repository.TimeDepositRepository;
import org.ikigaidigital.testdata.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TimeDepositServiceTest {

    private TimeDepositRepository timeDepositRepositoryMock;
    private TimeDepositMapper timeDepositMapperMock;
    private TimeDepositCalculator timeDepositCalculatorMock;

    private TimeDepositService target;

    @BeforeEach
    void setUp() {
        this.timeDepositRepositoryMock = mock(TimeDepositRepository.class);
        this.timeDepositMapperMock = mock(TimeDepositMapper.class);
        this.timeDepositCalculatorMock = mock(TimeDepositCalculator.class);

        this.target = new TimeDepositService(timeDepositRepositoryMock,
                                             timeDepositMapperMock,
                                             timeDepositCalculatorMock);
    }

    @Test
    @DisplayName("Should return an empty list when no time deposits are found")
    void getTimeDeposits_emptyList() {
        when(timeDepositRepositoryMock.findAllWithWithdrawals()).thenReturn(Collections.emptyList());

        var timeDeposits = target.getTimeDeposits();

        verify(timeDepositRepositoryMock, times(1)).findAllWithWithdrawals();
        verifyNoMoreInteractions(timeDepositRepositoryMock);

        assertNotNull(timeDeposits, "Time deposits should not be null");
        assertTrue(timeDeposits.isEmpty(), "Time deposits should be empty");
    }

    @Test
    @DisplayName("Should return a list of time deposits")
    void getTimeDeposits_listOfTimeDeposits() {
        var timeDeposit = TestUtils.aTimeDepositBuilder()
                                   .withId(1)
                                   .withPlanType(TimeDepositPlan.BASIC)
                                   .withBalance(new BigDecimal("1000.00"))
                                   .withDays(30)
                                   .withWithdrawals(Collections.emptyList())
                                   .build();
        var timeDeposits = Collections.singletonList(timeDeposit);
        when(timeDepositRepositoryMock.findAllWithWithdrawals()).thenReturn(timeDeposits);
        when(timeDepositMapperMock.toTimeDepositResponse(timeDeposit)).thenReturn(TimeDepositResponse.aBuilder()
                                                                                                     .withId(timeDeposit.getId())
                                                                                                     .withPlanType(
                                                                                                             TimeDepositResponse.PlanType.BASIC)
                                                                                                     .withBalance(
                                                                                                             timeDeposit.getBalance())
                                                                                                     .withDays(
                                                                                                             timeDeposit.getDays())
                                                                                                     .withWithdrawals(
                                                                                                             Collections.emptyList())
                                                                                                     .build());

        var response = target.getTimeDeposits();

        verify(timeDepositRepositoryMock, times(1)).findAllWithWithdrawals();
        verify(timeDepositMapperMock, times(1)).toTimeDepositResponse(timeDeposit);
        verifyNoMoreInteractions(timeDepositRepositoryMock);
        verifyNoMoreInteractions(timeDepositMapperMock);

        assertNotNull(response, "Time deposits should not be null");
        assertEquals(1, response.size(), "Time deposits size should be 1");
    }

    @Test
    @DisplayName("Should update all time deposit balances")
    void updateAllTimeDepositBalances() {
        var balance = new BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP);

        var timeDeposit = TestUtils.aTimeDepositBuilder()
                                   .withId(1)
                                   .withPlanType(TimeDepositPlan.BASIC)
                                   .withBalance(balance)
                                   .withDays(31)
                                   .withWithdrawals(Collections.emptyList())
                                   .build();
        var timeDeposits = List.of(timeDeposit);
        var internalTimeDeposit = new org.ikigaidigital.TimeDeposit(timeDeposit.getId(), timeDeposit.getPlanType().getRawPlanType(), balance.doubleValue(), timeDeposit.getDays());

        var updatedTimeDeposit = TestUtils.aTimeDepositBuilder()
                .withId(timeDeposit.getId())
                .withPlanType(timeDeposit.getPlanType())
                .withBalance(new BigDecimal("1000.83").setScale(2, RoundingMode.HALF_UP))
                .withDays(timeDeposit.getDays())
                .withWithdrawals(Collections.emptyList())
                .build();

        when(timeDepositRepositoryMock.findAll()).thenReturn(timeDeposits);
        when(timeDepositMapperMock.toTimeDepositInternal(timeDeposit)).thenReturn(internalTimeDeposit);
        doAnswer(invocation -> {
            internalTimeDeposit.setBalance(1000.83); // Simulate the balance update
            return internalTimeDeposit;
        }).when(timeDepositCalculatorMock).updateBalance(List.of(internalTimeDeposit));
        when(timeDepositMapperMock.toTimeDepositEntity(internalTimeDeposit)).thenReturn(updatedTimeDeposit);

        target.updateAllTimeDepositBalances();

        verify(timeDepositRepositoryMock, times(1)).findAll();
        verify(timeDepositMapperMock, times(1)).toTimeDepositInternal(timeDeposit);
        verify(timeDepositCalculatorMock, times(1)).updateBalance(List.of(internalTimeDeposit));
        verify(timeDepositRepositoryMock, times(1)).save(updatedTimeDeposit);
        verifyNoMoreInteractions(timeDepositRepositoryMock);
    }
}