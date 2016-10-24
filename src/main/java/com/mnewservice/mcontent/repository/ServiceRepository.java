package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity.DeliveryTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface ServiceRepository extends CrudRepository<ServiceEntity, Long> {

    ServiceEntity findByKeywordAndShortCodeAndOperator(
            String keyword, int shortCode, String operator);

    ServiceEntity findByUnsubscribeKeywordAndShortCodeAndOperator(
            String unsubscribeKeyword, int shortCode, String operator);

    List<ServiceEntity> findByDeliveryTime(DeliveryTime deliveryTime);

    List<ServiceEntity> findByOperatorNotIn(List<String> operators);
    List<ServiceEntity> findAllByOrderByOperatorAscShortCodeAscKeywordAsc();

    @Query("select se from ServiceEntity se "
            + "left join se.deliveryPipe dp "
            + "where dp.id = :pipeId")
    List<ServiceEntity> findAllByPipeId(@Param("pipeId") Long pipeId);
    
//    @Query("select se from ServiceEntity se "
//            + "left join se.deliveryPipe dp "
//            + "where dp.id = :pipeId"
//            + "order by se.operator asc, se.shortCode asc, se.keyword asc")
    List<ServiceEntity> findByDeliveryPipeIdInOrderByOperatorAscShortCodeAscKeywordAsc(List<Long> pipeIds);

    Long countByDeliveryPipeIdInOrderByOperatorAscShortCodeAscKeywordAsc(List<Long> pipeIds);

    @Query("select se from ServiceEntity se "
            + "where se.keyword like ?1 order by se.keyword asc")
    Collection<ServiceEntity> findAllByOrderByKeywordAsc(String nameFilter);
    /*
        @Query("select db from ProviderEntity db "
         + "where db.name like ?1 order by db.name asc")
    Collection<ProviderEntity> findAllByOrderByNameAsc(String nameFilter);
*/
    
    Collection<ServiceEntity> findByKeywordContainingOrderByOperatorAscShortCodeAscKeywordAsc(String nameFilter);

    Collection<ServiceEntity> findByDeliveryPipeIdInAndKeywordContainingOrderByOperatorAscShortCodeAscKeywordAsc(Collection<Long> deliveryPipeIds, String nameFilter);

}
