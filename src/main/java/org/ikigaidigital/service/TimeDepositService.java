package org.ikigaidigital.service;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.entity.TimeDeposit;
import org.ikigaidigital.mapper.TimeDepositMapper;
import org.ikigaidigital.repository.TimeDepositRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeDepositService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TimeDepositService.class);

    private final TimeDepositRepository timeDepositRepository;
    private final TimeDepositMapper mapper;
    private final TimeDepositCalculator timeDepositCalculator;

    public TimeDepositService(TimeDepositRepository timeDepositRepository,
                              TimeDepositMapper mapper,
                              TimeDepositCalculator timeDepositCalculator) {
        this.timeDepositRepository = timeDepositRepository;
        this.mapper = mapper;
        this.timeDepositCalculator = timeDepositCalculator;
    }

    @Transactional(readOnly = true)
    public List<TimeDepositResponse> getTimeDeposits() {
        List<TimeDeposit> deposits = timeDepositRepository.findAllWithWithdrawals();

        return deposits.stream().map(mapper::toTimeDepositResponse).toList();
    }

    public void updateAllTimeDepositBalances() {
        List<TimeDeposit> timeDeposits = timeDepositRepository.findAll();

        LOGGER.info("Calculating balances for {} time deposits.", timeDeposits.size());
        List<org.ikigaidigital.TimeDeposit> deposits = timeDeposits.stream()
                                                          .map(mapper::toTimeDepositInternal)
                                                          .collect(Collectors.toList());
        timeDepositCalculator.updateBalance(deposits);

        for (int i = 0; i < timeDeposits.size(); i++) {
            TimeDeposit timeDeposit = timeDeposits.get(i);
            org.ikigaidigital.TimeDeposit updatedInternalDeposit = deposits.get(i);
            if (updatedInternalDeposit.getBalance() != timeDeposit.getBalance().doubleValue()) {
                BigDecimal previousBalance = timeDeposit.getBalance();
                mapper.updateTimeDepositEntityFromInternal(timeDeposit, updatedInternalDeposit);
                LOGGER.info("Updating deposit id {} balance from: {} to: {}.",
                            timeDeposit.getId(), previousBalance, timeDeposit.getBalance());
            }
        }
        timeDepositRepository.saveAll(timeDeposits);
    }
}
