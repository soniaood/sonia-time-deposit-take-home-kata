package org.ikigaidigital.service;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.entity.TimeDeposit;
import org.ikigaidigital.mapper.TimeDepositMapper;
import org.ikigaidigital.repository.TimeDepositRepository;
import org.ikigaidigital.util.TimeDepositConstants;
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

        for (TimeDeposit timeDeposit : timeDeposits) {
            org.ikigaidigital.TimeDeposit updatedInternalDeposit = deposits.get(timeDeposits.indexOf(timeDeposit));
            BigDecimal updatedBalance = BigDecimal.valueOf(updatedInternalDeposit.getBalance())
                    .setScale(TimeDepositConstants.FINANCIAL_SCALE, TimeDepositConstants.FINANCIAL_ROUNDING_MODE);
            if (updatedBalance.compareTo(timeDeposit.getBalance()) != 0) {
                LOGGER.info("Updating deposit id {} balance from: {} to: {}.",
                            timeDeposit.getId(), timeDeposit.getBalance(), updatedBalance);
                timeDeposit.setBalance(updatedBalance);
                timeDepositRepository.save(timeDeposit);
            }
        }
    }
}
