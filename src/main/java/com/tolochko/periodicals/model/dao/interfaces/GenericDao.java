package com.tolochko.periodicals.model.dao.interfaces;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<E, T extends Serializable> {

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be null
     * @return the entity with the given id or {@code null} if none found
     */
    E findOneById(T id);

    /**
     * Returns all entities from the db.
     *
     * @return all entities
     */
    List<E> findAll();

    /**
     * Creates a new entity taking values for the fields from the passed entity.
     * If a passed entity has the 'id' field it is ignored.
     *
     * @param entity an object to be persisted
     * @return a persisted entity's id
     * @throws IllegalArgumentException in case the given entity is null
     */
    long add(E entity);

    /**
     * Updates an entity in the db.
     *
     * @param entity an object to be updated
     */
    int updateById(T id, E entity);

}
