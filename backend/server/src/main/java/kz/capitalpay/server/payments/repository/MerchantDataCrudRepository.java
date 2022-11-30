package kz.capitalpay.server.payments.repository;

import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.payments.dto.MerchantData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MerchantDataCrudRepository extends CrudRepository<ApplicationUser, Long> {

    @Query(value = "select distinct application_user.id    as merchantId, " +
            "                application_user.email as email, " +
            "                kyc1.fieldValue       as name, " +
            "                kyc2.fieldValue       as phone " +
            "from ApplicationUser application_user" +
            "         join MerchantKyc kyc1 on kyc1. merchantId = application_user.id " +
            "         join MerchantKyc kyc2 on kyc2. merchantId = application_user.id " +
            "         join Payment payment on payment. merchantId = application_user.id " +
            "where (lower(application_user.email) like concat('%', lower(?1), '%') and kyc2. fieldName = 'mainphone' and " +
            "       kyc1. fieldName = 'mname') " +
            "   or (kyc1. fieldName = 'mname' and lower(kyc1. fieldValue) like concat('%', lower(?1), '%') and " +
            "       kyc2. fieldName = 'mainphone') " +
            "   or (kyc2. fieldName = 'mainphone' and lower(kyc2. fieldValue) like concat('%', lower(?1), '%') and " +
            "       kyc1. fieldName = 'mname')")
    List<MerchantData> findDataMerchantByText(String text);
}
