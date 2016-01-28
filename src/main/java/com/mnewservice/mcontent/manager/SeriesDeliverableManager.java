package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.domain.mapper.FileMapper;
import com.mnewservice.mcontent.domain.mapper.SeriesDeliverableMapper;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import com.mnewservice.mcontent.util.ShortUrlUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
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
    private DeliveryPipeRepository deliveryPipeRepository;

    @Autowired
    private SeriesDeliverableRepository seriesRepository;

    @Autowired
    private SeriesDeliverableMapper seriesMapper;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ContentRepository contentRepository;

    private static final Logger LOG = Logger.getLogger(SeriesDeliverableManager.class);

    @Transactional(readOnly = true)
    public Integer getNextDeliveryDay(Long deliveryPipeId) {
        LOG.info("Getting next delivery day with deliveryPipeId=" + deliveryPipeId);
        return seriesRepository.countByDeliveryPipeId(deliveryPipeId).intValue() + 1;
    }

    @Transactional
    public ContentFile addFile(Long contentId, ContentFile file) {
        LOG.info("Adding file [" + file.getFilename() + "] to series contentId=" + contentId);
        SeriesDeliverableEntity entity = seriesRepository.findOne(contentId);
        String errMsg = "";
        if (entity == null) {
            errMsg = "Don't find series from repository with contentId=" + contentId;
            LOG.error(errMsg);
            file.setAccepted(false);
            file.setErrorMessage(errMsg);
            return file;
        }
        ContentFile repositoryFile = fileManager.getFile(file.getId());
        if (repositoryFile == null) {
            errMsg = "Don't find file from repository with fileId=" + file.getId();
            LOG.error(errMsg);
            file.setAccepted(false);
            file.setErrorMessage(errMsg);
            return file;
        }
        file = repositoryFile;
        List<FileEntity> fileList = entity.getFiles();
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        fileList.add(fileMapper.toEntity(file));
        entity.setFiles(fileList);
        seriesRepository.save(entity);
        return file;
    }

    @Transactional(readOnly = true)
    public Collection<SeriesDeliverable> getDeliveryPipeSeriesContent(long id) {
        LOG.info("Getting series content for delivery with id=" + id);
        DeliveryPipeEntity entity = deliveryPipeRepository.findOne(id);
        List<SeriesDeliverableEntity> contents = seriesRepository.findByDeliveryPipeOrderByDeliveryDaysAfterSubscriptionAsc(entity);
        return seriesMapper.toDomain(contents);
    }

    @Transactional(readOnly = true)
    public SeriesDeliverable getSeriesContent(long id) {
        SeriesDeliverableEntity content = seriesRepository.findOne(id);
        return seriesMapper.toDomain(content);
    }

    public SeriesDeliverable saveSeriesContent(long deliveryPipeId, SeriesDeliverable deliverable) {
        SeriesDeliverableEntity entity = seriesMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setStatus(AbstractDeliverableEntity.DeliverableStatusEnum.PENDING_APPROVAL);
            entity.setDeliveryPipe(deliveryPipeRepository.findOne(deliveryPipeId));
            entity.setDeliveryDaysAfterSubscription((int) (seriesRepository.countByDeliveryPipeId(deliveryPipeId) + 1));
        }
        if (entity.getContent().getShortUuid() == null) {
            String shortUuid;
            while (contentRepository.findByShortUuid(shortUuid = ShortUrlUtils.getRandomShortIdentifier()) != null);
            entity.getContent().setShortUuid(shortUuid);
        }

        // TODO: for the providers: allow save if and only if status == PENDING_APPROVAL
        return seriesMapper.toDomain(seriesRepository.save(entity));
    }

    @Transactional
    public void removeSeriesContent(Long id) {
        LOG.info("Removing series content with id=" + id);
        SeriesDeliverableEntity entity = seriesRepository.findOne(id);
        seriesRepository.delete(entity);
    }

}
