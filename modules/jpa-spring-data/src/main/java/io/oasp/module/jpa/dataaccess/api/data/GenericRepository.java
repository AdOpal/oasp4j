package io.oasp.module.jpa.dataaccess.api.data;

import static com.querydsl.core.alias.Alias.$;

import java.io.Serializable;
import java.util.Collection;

import net.sf.mmm.util.exception.api.ObjectNotFoundException;
import net.sf.mmm.util.exception.api.ObjectNotFoundUserException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPADeleteClause;

import io.oasp.module.jpa.dataaccess.api.QueryDslUtil;
import io.oasp.module.jpa.dataaccess.api.feature.FeatureForceIncrementModificationCounter;

/**
 * {@link JpaRepository} with {@link QueryDslSupport} as well as typical OASP standard operations. It is recommended to
 * use {@link DefaultRepository} instead.
 *
 * @param <E> generic type of the managed {@link #getEntityClass() entity}. Typically implementing
 *        {@link net.sf.mmm.util.entity.api.PersistenceEntity}.
 * @param <ID> generic type of the {@link net.sf.mmm.util.entity.api.PersistenceEntity#getId() primary key} of the
 *        entity.
 *
 * @since 3.0.0
 */
public interface GenericRepository<E, ID extends Serializable>
    extends JpaRepository<E, ID>, QueryDslSupport<E>, FeatureForceIncrementModificationCounter<E> {

  /**
   * @return the {@link Class} of the managed entity.
   */
  Class<E> getEntityClass();

  /**
   * @param id the {@link net.sf.mmm.util.entity.api.PersistenceEntity#getId() primary key}. May not be {@code null}.
   * @return the requested entity. Never {@code null}.
   * @throws ObjectNotFoundException if the requested entity does not exist.
   * @see #findOne(java.io.Serializable)
   */
  default E find(ID id) throws ObjectNotFoundException {

    E entity = findOne(id);
    if (entity == null) {
      throw new ObjectNotFoundUserException(getEntityClass(), id);
    }
    return entity;
  }

  /**
   * @param ids the {@link Collection} of {@link net.sf.mmm.util.entity.api.PersistenceEntity#getId() IDs} to delete.
   * @return the number of entities that have actually been deleted.
   */
  @Modifying
  default long deleteByIds(Collection<ID> ids) {

    if ((ids == null) || (ids.isEmpty())) {
      return 0;
    }
    E alias = newDslAlias();
    EntityPathBase<E> entityPath = $(alias);
    JPADeleteClause delete = newDslDeleteClause(entityPath);
    @SuppressWarnings("rawtypes")
    Class idType = ids.iterator().next().getClass();
    // https://github.com/querydsl/querydsl/issues/2085
    @SuppressWarnings("unchecked")
    SimpleExpression<ID> idPath = Expressions.numberPath(idType, entityPath, "id");
    BooleanExpression inClause = QueryDslUtil.get().newInClause(idPath, ids);
    delete.where(inClause);
    return delete.execute();
  }

}
