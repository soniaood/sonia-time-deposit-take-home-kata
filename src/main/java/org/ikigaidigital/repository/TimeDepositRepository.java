package org.ikigaidigital.repository;

import org.ikigaidigital.entity.TimeDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeDepositRepository extends JpaRepository<TimeDeposit, Integer> {

    @Query("SELECT td FROM TimeDeposit td JOIN FETCH td.withdrawals")
    List<TimeDeposit> findAllWithWithdrawals();
}
