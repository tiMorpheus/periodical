package com.tolochko.periodicals.model.service.impl;

import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.PeriodicalDao;
import com.tolochko.periodicals.model.domain.PeriodicalNumberByCategory;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.service.PeriodicalService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class PeriodicalServiceImpl implements PeriodicalService {
    private static final Logger logger = Logger.getLogger(PeriodicalServiceImpl.class);
    private DaoFactory factory = MySqlDaoFactory.getFactoryInstance();


    @Override
    public Periodical findOneById(long id) {
            logger.debug("looking for an periodical with id:" + id);

            return factory.getPeriodicalDao().findOneById(id);
    }

    @Override
    public Periodical findOneByName(String name) {
            return factory.getPeriodicalDao().findOneByName(name);
    }

    @Override
    public List<Periodical> findAll() {
            return factory.getPeriodicalDao().findAll();
    }

    @Override
    public List<Periodical> findAllByStatus(Periodical.Status status) {
            return factory.getPeriodicalDao().findAllByStatus(status);
    }

    @Override
    public Periodical save(Periodical periodical) {

        if (periodical.getId() == 0) {
            createNewPeriodical(periodical);
        } else {
            updatePeriodical(periodical);
        }

        return getPeriodicalFromDbByName(periodical.getName());
    }


    private void createNewPeriodical(Periodical periodical) {
            factory.getPeriodicalDao().add(periodical);
    }

    private void updatePeriodical(Periodical periodical) {
            int affectedRows = factory.getPeriodicalDao().
                    updateById(periodical.getId(), periodical);

            if (affectedRows == 0) {
                throw new NoSuchElementException(
                        String.format("There is no periodical in the DB with id = %d", periodical.getId()));
            }
    }

    private Periodical getPeriodicalFromDbByName(String name) {
            return factory.getPeriodicalDao().findOneByName(name);
    }

    @Override
    public int updateAndSetDiscarded(Periodical periodical) {
            return factory.getPeriodicalDao().updateAndSetDiscarded(periodical);
    }

    @Override
    public int deleteAllDiscarded() {
            return factory.getPeriodicalDao().deleteAllDiscarded();
    }

    @Override
    public boolean hasActiveSubscriptions(long periodicalId) {
            return !factory.getSubscriptionDao()
                    .findAllByPeriodicalIdAndStatus(periodicalId, Subscription.Status.ACTIVE)
                    .isEmpty();
    }

    @Override
    public List<PeriodicalNumberByCategory> getQuantitativeStatistics() {
        List<PeriodicalNumberByCategory> statistics = new ArrayList<>();

            PeriodicalDao dao = factory.getPeriodicalDao();

            for (PeriodicalCategory category : PeriodicalCategory.values()) {
                statistics.add(getPeriodicalNumberByCategory(dao, category));
            }

            return statistics;
    }

    private PeriodicalNumberByCategory getPeriodicalNumberByCategory(PeriodicalDao dao,
                                                                     PeriodicalCategory category) {
        int active = dao.findNumberOfPeriodicalsWithCategoryAndStatus(category,
                Periodical.Status.ACTIVE);
        int inActive = dao.findNumberOfPeriodicalsWithCategoryAndStatus(category,
                Periodical.Status.INACTIVE);
        int discarded = dao.findNumberOfPeriodicalsWithCategoryAndStatus(category,
                Periodical.Status.DISCARDED);

        return PeriodicalNumberByCategory.newBuilder(category)
                .setActive(active)
                .setInActive(inActive)
                .setDiscarded(discarded)
                .build();
    }

}
