package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankAccountEntity b WHERE b.accountNumber = :accountNumber")
    Optional<BankAccountEntity> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);


    Optional<BankAccountEntity> findByAccountNumber(String accountNumber);


    boolean existsByAccountNumber(String accountNumber);


    List<BankAccountEntity> findByClient_Id(Long clientId);


    long countByClient_Id(Long clientId);

}
