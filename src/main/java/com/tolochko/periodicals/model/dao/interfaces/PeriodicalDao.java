package com.tolochko.periodicals.model.dao.interfaces;

import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;

import java.util.List;

public interface PeriodicalDao extends GenericDao<Periodical, Long> {

    Periodical findOneByName(String name);

    List<Periodical> findAllByStatus(Periodical.Status status);

    int findNumberOfPeriodicalsWithCategoryAndStatus(PeriodicalCategory category, Periodical.Status status);

    /**
     * Updates a periodical and sets a new status 'discarded' only if there is no active subscriptions
     * of this periodical.
     *
     * @return the number of affected rows: 0 - if the condition was not satisfied and updated
     * has not happened; 1 - if the status of this periodical has been changed to 'discarded'
     */
    int updateAndSetDiscarded(Periodical periodical);

    /**
     * Deletes from the db all periodicals with status = 'discarded'.
     *
     * @return the number of deleted periodicals.
     */
    int deleteAllDiscarded();

    int addIntoArchive(Periodical periodical);

    void delete(long id);

}
