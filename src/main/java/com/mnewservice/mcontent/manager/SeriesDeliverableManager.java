package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class SeriesDeliverableManager {

    @Autowired
    private SeriesDeliverableRepository seriesRepository;

    @Transactional(readOnly = true)
    public Integer getNextDeliveryDay(Long deliveryPipeId) {
        return seriesRepository.countByDeliveryPipeId(deliveryPipeId).intValue() + 1;
    }

}
